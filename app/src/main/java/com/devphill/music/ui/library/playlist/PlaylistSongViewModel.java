package com.devphill.music.ui.library.playlist;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.devphill.music.R;
import com.devphill.music.model.Song;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.ui.library.SongViewModel;
import com.devphill.music.ui.library.album.AlbumActivity;
import com.devphill.music.ui.library.artist.ArtistActivity;

import java.util.List;

import timber.log.Timber;

public class PlaylistSongViewModel extends SongViewModel {

    private Context mContext;
    private OnPlaylistEntriesChangeListener mRemoveListener;

    public PlaylistSongViewModel(BaseFragment fragment, List<Song> songs,
                                 OnPlaylistEntriesChangeListener listener) {
        super(fragment, songs);
        mContext = fragment.getContext();
        mRemoveListener = listener;
    }

    public interface OnPlaylistEntriesChangeListener {
        void onPlaylistEntriesChange();
    }

    @Override
    public View.OnClickListener onClickMenu() {
        return v -> {
            final PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
            menu.inflate(R.menu.instance_song_playlist);
            menu.setOnMenuItemClickListener(onMenuItemClick(v));
            menu.show();
        };
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClick(View view) {
        return menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_queue_item_next:
                    mPlayerController.queueNext(getReference());
                    return true;
                case R.id.menu_item_queue_item_last:
                    mPlayerController.queueLast(getReference());
                    return true;
                case R.id.menu_item_navigate_to_artist:
                    mMusicStore.findArtistById(getReference().getArtistId()).subscribe(
                            artist -> {
                                mContext.startActivity(ArtistActivity.newIntent(mContext, artist));
                            }, throwable -> {
                                Timber.e(throwable, "Failed to find artist");
                            });

                    return true;
                case R.id.menu_item_navigate_to_album:
                    mMusicStore.findAlbumById(getReference().getAlbumId()).subscribe(
                            album -> {
                                mContext.startActivity(AlbumActivity.newIntent(mContext, album));
                            }, throwable -> {
                                Timber.e(throwable, "Failed to find album");
                            });

                    return true;
                case R.id.menu_item_remove:
                    removeFromPlaylist(view);
                    return true;
            }
            return false;
        };
    }

    private void removeFromPlaylist(View snackbarContainer) {
        Song removed = getReference();
        int removedIndex = getIndex();

        getSongs().remove(getIndex());
        mRemoveListener.onPlaylistEntriesChange();

        String songName = removed.getSongName();
        String message = mContext.getString(R.string.message_removed_song, songName);

        Snackbar.make(snackbarContainer, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo, view -> {
                    getSongs().add(removedIndex, removed);
                    mRemoveListener.onPlaylistEntriesChange();
                }).show();
    }
}
