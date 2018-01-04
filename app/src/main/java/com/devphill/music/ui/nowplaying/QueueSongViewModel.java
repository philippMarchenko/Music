package com.devphill.music.ui.nowplaying;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.devphill.music.R;
import com.devphill.music.ui.library.album.AlbumActivity;
import com.devphill.music.ui.library.artist.ArtistActivity;
import com.devphill.music.ui.common.playlist.AppendPlaylistDialogFragment;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.model.Song;
import com.devphill.music.ui.library.SongViewModel;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class QueueSongViewModel extends SongViewModel {

    private static final String TAG_PLAYLIST_DIALOG = "QueueSongViewModel.PlaylistDialog";

    private Context mContext;
    private FragmentManager mFragmentManager;
    private OnRemoveListener mRemoveListener;

    public QueueSongViewModel(BaseFragment fragment, List<Song> songs,
                              OnRemoveListener removeListener) {
        super(fragment, songs);
        mContext = fragment.getContext();
        mFragmentManager = fragment.getFragmentManager();
        mRemoveListener = removeListener;
    }

    public interface OnRemoveListener {
        void onRemove();
    }

    @Override
    protected Observable<Boolean> isPlaying() {
        return mPlayerController.getQueuePosition().map(position -> position == getIndex());
    }

    @Override
    public View.OnClickListener onClickSong() {
        return v -> mPlayerController.changeSong(getIndex());
    }

    @Override
    public View.OnClickListener onClickMenu() {
        return v -> {
            PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
            menu.inflate(getReference().isInLibrary()
                    ? R.menu.instance_song_queue
                    : R.menu.instance_song_queue_remote);
            menu.setOnMenuItemClickListener(onMenuItemClick(v));
            menu.show();
        };
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClick(View view) {
        return menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_navigate_to_artist:
                    navigateToArtist();
                    return true;
                case R.id.menu_item_navigate_to_album:
                    navigateToAlbum();
                    return true;
                case R.id.menu_item_add_to_playlist:
                    addToPlaylist();
                    return true;
                case R.id.menu_item_remove:
                    removeFromQueue(view);
                    return true;
            }
            return false;
        };
    }

    private void navigateToArtist() {
        mMusicStore.findArtistById(getReference().getArtistId()).subscribe(
                artist -> {
                    mContext.startActivity(ArtistActivity.newIntent(mContext, artist));
                }, throwable -> {
                    Timber.e(throwable, "Failed to find artist");
                });
    }

    private void navigateToAlbum() {
        mMusicStore.findAlbumById(getReference().getAlbumId()).subscribe(
                album -> {
                    mContext.startActivity(AlbumActivity.newIntent(mContext, album));
                }, throwable -> {
                    Timber.e(throwable, "Failed to find album");
                });
    }

    private void addToPlaylist() {
        new AppendPlaylistDialogFragment.Builder(mContext, mFragmentManager)
                .setTitle(mContext.getResources().getString(
                        R.string.header_add_song_name_to_playlist, getReference()))
                .setSongs(getReference())
                .showSnackbarIn(R.id.now_playing_artwork)
                .show(TAG_PLAYLIST_DIALOG);
    }

    private void removeFromQueue(View snackbarContainer) {
        mPlayerController.getQueuePosition().take(1)
                .subscribe(oldQueuePosition -> {
                    int itemPosition = getIndex();

                    getSongs().remove(itemPosition);

                    int newQueuePosition = (oldQueuePosition > itemPosition)
                            ? oldQueuePosition - 1
                            : oldQueuePosition;

                    newQueuePosition = Math.min(newQueuePosition, getSongs().size() - 1);
                    newQueuePosition = Math.max(newQueuePosition, 0);

                    mPlayerController.editQueue(getSongs(), newQueuePosition);

                    if (oldQueuePosition == itemPosition) {
                        mPlayerController.play();
                    }

                    mRemoveListener.onRemove();

                    Song removed = getReference();
                    String message = mContext.getString(R.string.message_removed_song,
                            removed.getSongName());

                    Snackbar.make(snackbarContainer, message, LENGTH_LONG)
                            .setAction(R.string.action_undo, v -> {
                                getSongs().add(itemPosition, removed);
                                mPlayerController.editQueue(getSongs(), oldQueuePosition);
                                if (oldQueuePosition == itemPosition) {
                                    mPlayerController.play();
                                }
                                mRemoveListener.onRemove();
                            })
                            .show();
                }, throwable -> {
                    Timber.e(throwable, "Failed to remove song from queue");
                });
    }
}
