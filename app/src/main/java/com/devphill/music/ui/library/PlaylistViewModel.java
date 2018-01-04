package com.devphill.music.ui.library;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;

import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.ui.library.playlist.AutoPlaylistActivity;
import com.devphill.music.ui.library.playlist.edit.AutoPlaylistEditActivity;
import com.devphill.music.ui.library.playlist.PlaylistActivity;
import com.devphill.music.data.store.PlaylistStore;
import com.devphill.music.model.AutoPlaylist;
import com.devphill.music.model.Playlist;
import com.devphill.music.player.PlayerController;

import javax.inject.Inject;

import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PlaylistViewModel extends BaseObservable {

    @Inject PlaylistStore mPlaylistStore;
    @Inject PlayerController mPlayerController;

    private Context mContext;
    private Playlist mPlaylist;

    public PlaylistViewModel(Context context) {
        mContext = context;
        JockeyApplication.getComponent(mContext).inject(this);
    }

    public void setPlaylist(Playlist playlist) {
        mPlaylist = playlist;
        notifyChange();
    }

    public String getName() {
        return mPlaylist.getPlaylistName();
    }

    public int getSmartIndicatorVisibility() {
        if (mPlaylist instanceof AutoPlaylist) {
            return VISIBLE;
        } else {
            return GONE;
        }
    }

    public View.OnClickListener onClickPlaylist() {
        return v -> {
            Intent intent;
            if (mPlaylist instanceof AutoPlaylist) {
                intent = AutoPlaylistActivity.newIntent(mContext, (AutoPlaylist) mPlaylist);
            } else {
                intent = PlaylistActivity.newIntent(mContext, mPlaylist);
            }
            mContext.startActivity(intent);
        };
    }

    public View.OnClickListener onClickMenu() {
        return v -> {
            PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
            menu.inflate((mPlaylist instanceof AutoPlaylist)
                    ? R.menu.instance_smart_playlist
                    : R.menu.instance_playlist);

            menu.setOnMenuItemClickListener(onMenuItemClick(v));
            menu.show();
        };
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClick(View view) {
        return menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_queue_item_next:
                    queuePlaylistNext();
                    return true;
                case R.id.menu_item_queue_item_last:
                    queuePlaylistLast();
                    return true;
                case R.id.menu_item_edit:
                    editThisAsAutoPlaylist();
                    return true;
                case R.id.menu_item_delete:
                    if (mPlaylist instanceof AutoPlaylist) {
                        deleteAutoPlaylist(view);
                    } else {
                        deletePlaylist(view);
                    }
                    return true;
            }
            return false;
        };
    }

    private void queuePlaylistNext() {
        mPlaylistStore.getSongs(mPlaylist).subscribe(
                mPlayerController::queueNext,
                throwable -> {
                    Timber.e(throwable, "Failed to get songs");
                });
    }

    private void queuePlaylistLast() {
        mPlaylistStore.getSongs(mPlaylist).subscribe(
                mPlayerController::queueLast,
                throwable -> {
                    Timber.e(throwable, "Failed to get songs");
                });
    }

    private void editThisAsAutoPlaylist() {
        AutoPlaylist autoPlaylist = (AutoPlaylist) mPlaylist;
        Intent intent = AutoPlaylistEditActivity.newIntent(mContext, autoPlaylist);
        mContext.startActivity(intent);
    }

    private void deletePlaylist(View snackbarContainer) {
        Playlist removed = mPlaylist;
        String playlistName = mPlaylist.getPlaylistName();
        String message = mContext.getString(R.string.message_removed_playlist, playlistName);

        mPlaylistStore.getSongs(removed)
                .subscribe(originalContents -> {
                    mPlaylistStore.removePlaylist(removed);

                    Snackbar.make(snackbarContainer, message, LENGTH_LONG)
                            .setAction(R.string.action_undo, view -> {
                                mPlaylistStore.makePlaylist(playlistName, originalContents);
                            })
                            .show();
                }, throwable -> {
                    Timber.e(throwable, "Failed to get playlist contents");

                    // If we can't get the original contents of the playlist, remove it anyway but
                    // don't give an undo option
                    mPlaylistStore.removePlaylist(removed);
                    Snackbar.make(snackbarContainer, message, LENGTH_LONG).show();
                });
    }

    private void deleteAutoPlaylist(View snackbarContainer) {
        mPlaylistStore.removePlaylist(mPlaylist);

        String playlistName = mPlaylist.getPlaylistName();
        String message = mContext.getString(R.string.message_removed_playlist, playlistName);
        AutoPlaylist removed = (AutoPlaylist) mPlaylist;

        Snackbar.make(snackbarContainer, message, LENGTH_LONG)
                .setAction(R.string.action_undo, view -> {
                    mPlaylistStore.makePlaylist(removed);
                })
                .show();
    }

}
