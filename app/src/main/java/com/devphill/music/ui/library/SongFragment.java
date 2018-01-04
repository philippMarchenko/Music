package com.devphill.music.ui.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marverenic.adapter.HeterogeneousAdapter;
import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.ui.common.LibraryEmptyState;
import com.devphill.music.view.HeterogeneousFastScrollAdapter;
import com.devphill.music.ui.common.ShuffleAllSection;
import com.devphill.music.data.store.MusicStore;
import com.devphill.music.model.Song;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.view.BackgroundDecoration;
import com.devphill.music.view.DividerDecoration;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class SongFragment extends BaseFragment {

    @Inject MusicStore mMusicStore;

    private FastScrollRecyclerView mRecyclerView;
    private HeterogeneousAdapter mAdapter;
    private ShuffleAllSection mShuffleAllSection;
    private SongSection mSongSection;
    private List<Song> mSongs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JockeyApplication.getComponent(this).inject(this);
        mMusicStore.getSongs()
                .compose(bindToLifecycle())
                .subscribe(
                        songs -> {
                            mSongs = songs;
                            setupAdapter();
                        },
                        throwable -> Timber.e(throwable, "Failed to get new songs"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library_page, container, false);
        mRecyclerView = (FastScrollRecyclerView) view.findViewById(R.id.library_page_list);
        mRecyclerView.addItemDecoration(new BackgroundDecoration());
        mRecyclerView.addItemDecoration(new DividerDecoration(getContext(), R.id.empty_layout));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        if (mAdapter == null) {
            setupAdapter();
        } else {
            mRecyclerView.setAdapter(mAdapter);
        }

        int paddingH = (int) getActivity().getResources().getDimension(R.dimen.global_padding);
        view.setPadding(paddingH, 0, paddingH, 0);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        mAdapter = null;
        mSongSection = null;
    }

    private void setupAdapter() {
        if (mRecyclerView == null || mSongs == null) {
            return;
        }

        if (mSongSection != null && mShuffleAllSection != null) {
            mSongSection.setData(mSongs);
            mShuffleAllSection.setData(mSongs);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new HeterogeneousFastScrollAdapter();
            mAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mAdapter);

            mSongSection = new SongSection(this, mSongs);
            mShuffleAllSection = new ShuffleAllSection(this, mSongs);
            mAdapter.addSection(mShuffleAllSection);
            mAdapter.addSection(mSongSection);
            mAdapter.setEmptyState(new LibraryEmptyState(getActivity()));
        }
    }
}
