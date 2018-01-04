package com.devphill.music.ui.library.playlist;

import android.content.Intent;
import android.databinding.Bindable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;

import com.marverenic.adapter.DragDropAdapter;
import com.marverenic.adapter.DragDropDecoration;
import com.devphill.music.R;
import com.devphill.music.data.store.PlaylistStore;
import com.devphill.music.model.AutoPlaylist;
import com.devphill.music.model.Playlist;
import com.devphill.music.model.Song;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.ui.BaseViewModel;
import com.devphill.music.ui.common.LibraryEmptyState;
import com.devphill.music.ui.common.ShuffleAllSection;
import com.devphill.music.ui.library.playlist.edit.AutoPlaylistEditActivity;
import com.devphill.music.view.DragBackgroundDecoration;
import com.devphill.music.view.DragDividerDecoration;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PlaylistViewModel extends BaseViewModel {

    // TODO Refactor list view models and remove this field
    private BaseFragment mFragment;

    private PlaylistStore mPlaylistStore;
    private Playlist mPlaylist;

    private DragDropAdapter mAdapter;
    private PlaylistSongSection mSongSection;
    private ShuffleAllSection mShuffleAllSection;

    public PlaylistViewModel(BaseFragment fragment, PlaylistStore playlistStore, Playlist playlist) {
        super(fragment);
        mFragment = fragment;
        mPlaylistStore = playlistStore;
        mPlaylist = playlist;

        createAdapter();
    }

    private void createAdapter() {
        mAdapter = new DragDropAdapter();
        mAdapter.setHasStableIds(true);

        mAdapter.setEmptyState(new LibraryEmptyState(mFragment.getActivity()) {
            @Override
            public String getEmptyMessage() {
                if (mPlaylist instanceof AutoPlaylist) {
                    return getString(R.string.empty_auto_playlist);
                } else {
                    return getString(R.string.empty_playlist);
                }
            }

            @Override
            public String getEmptyMessageDetail() {
                if (mPlaylist instanceof AutoPlaylist) {
                    return getString(R.string.empty_auto_playlist_detail);
                } else {
                    return getString(R.string.empty_playlist_detail);
                }
            }

            @Override
            public String getEmptyAction1Label() {
                if (mPlaylist instanceof AutoPlaylist) {
                    return getString(R.string.action_edit_playlist_rules);
                } else {
                    return "";
                }
            }

            @Override
            public void onAction1() {
                if (mPlaylist instanceof AutoPlaylist) {
                    AutoPlaylist playlist = (AutoPlaylist) mPlaylist;
                    Intent intent = AutoPlaylistEditActivity.newIntent(getContext(), playlist);

                    getContext().startActivity(intent);
                }
            }
        });

        mPlaylistStore.getSongs(mPlaylist)
                .compose(bindToLifecycle())
                .distinctUntilChanged()
                .map(ArrayList::new)
                .subscribe(this::setupAdapter, throwable -> {
                    Timber.e(throwable, "Failed to get playlist contents");
                });
    }

    private void setupAdapter(List<Song> playlistSongs) {
        if (mSongSection == null || mShuffleAllSection == null) {
            mSongSection = new PlaylistSongSection(mFragment, mPlaylistStore, playlistSongs, mPlaylist);
            mShuffleAllSection = new ShuffleAllSection(mFragment, playlistSongs);
            mAdapter.addSection(mShuffleAllSection);
            mAdapter.setDragSection(mSongSection);
        } else {
            mShuffleAllSection.setData(playlistSongs);
            mSongSection.setData(playlistSongs);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Bindable
    public DragDropAdapter getAdapter() {
        return mAdapter;
    }

    @Bindable
    public LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Bindable
    public ItemDecoration[] getItemDecorations() {
        NinePatchDrawable dragShadow = (NinePatchDrawable) ContextCompat.getDrawable(
                getContext(), R.drawable.list_drag_shadow);

        return new ItemDecoration[] {
                new DragBackgroundDecoration(R.id.song_drag_root),
                new DragDividerDecoration(R.id.song_drag_root, getContext(), R.id.empty_layout),
                new DragDropDecoration(dragShadow)
        };
    }

}
