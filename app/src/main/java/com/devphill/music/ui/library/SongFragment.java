package com.devphill.music.ui.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devphill.music.lastfm.api.LastFmApi;
import com.devphill.music.lastfm.api.LastFmService;
import com.devphill.music.lastfm.api.model.LfmArtistResponse;
import com.devphill.music.lastfm.model.Image;
import com.devphill.music.lastfm.model.LfmArtist;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import timber.log.Timber;

public class SongFragment extends BaseFragment {

    @Inject MusicStore mMusicStore;

    private FastScrollRecyclerView mRecyclerView;
    private HeterogeneousAdapter mAdapter;
    private ShuffleAllSection mShuffleAllSection;
    private SongSection mSongSection;
    private List<Song> mSongs;
    private static final String LOG_TAG = "SongFragment";

    public static final String SEARCH_ALL_SONGS = "search_all_songs";
    private BroadcastReceiver br;

    LastFmService lastFmService;

    private SimpleArrayMap<String, Observable<LfmArtist>> mCachedArtistInfo;
    private LfmArtist mLfmArtist;

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
                            for(int i = 0; i < mSongs.size(); i++){

                                getArtistInfo(mSongs.get(i).getArtistName());

                                int finalI = i;
                                getArtistInfo(mSongs.get(i).getArtistName())
                                        .compose(bindToLifecycle())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                lfmArtist -> {
                                                    Image hero = lfmArtist.getImageBySize(Image.Size.MEDIUM);
                                                    mSongs.get(finalI).setArtistImageUrl(hero.getUrl());
                                                    Log.d(LOG_TAG, "name  " + mSongs.get(finalI).getArtistName());
                                                    Log.d(LOG_TAG, "getArtistInfo " + hero.getUrl());
                                                    mSongSection.setData(mSongs);
                                                    mAdapter.notifyDataSetChanged();
                                                },
                                                throwable -> {
                                                    Timber.e(throwable, "Failed to get Last.fm artist info");
                                                });


                            }
                        },
                        throwable -> Timber.e(throwable, "Failed to get new songs"));

        lastFmService = LastFmApi.getService(getContext());
        mCachedArtistInfo = new SimpleArrayMap<>();


        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(LOG_TAG, intent.getCharSequenceExtra("search").toString());
                String charString = intent.getCharSequenceExtra("search").toString();
                if (charString.isEmpty()) {
                    mSongSection.setData(mSongs);
                    mAdapter.notifyDataSetChanged();
                } else {
                    List<Song> filteredList = new ArrayList<>();
                    for (Song row : mSongs) {
                        if (row.getSongName().toLowerCase().contains(charString.toLowerCase())
                                || row.getAlbumName().toLowerCase().contains(charString.toLowerCase())
                                || row.getArtistName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mSongSection.setData(filteredList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        getContext().registerReceiver(br,new IntentFilter(SEARCH_ALL_SONGS));
    }


    public Observable<LfmArtist> getArtistInfo(String artistName) {
        Observable<LfmArtist> result = mCachedArtistInfo.get(artistName);
        if (result == null) {
            result = lastFmService.getArtistInfo(artistName)
                    .map(response -> {
                        if (!response.isSuccessful()) {
                            String message = "Call to getArtistInfo failed with response code "
                                    + response.code()
                                    + "\n" + response.message();

                            throw Exceptions.propagate(new IOException(message));
                        }

                        return response.body().getArtist();
                    })
                    .cache();

            mCachedArtistInfo.put(artistName, result);
        }
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library_page, container, false);
        mRecyclerView =  view.findViewById(R.id.library_page_list);
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

        try {
            getContext().unregisterReceiver(br);
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Не удалось снять с регистрации приемник" + e.getMessage());
        }

    }

    private void setupAdapter() {
        if (mRecyclerView == null || mSongs == null) {
            return;
        }

        if (mSongSection != null ) {
            mSongSection.setData(mSongs);
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new HeterogeneousFastScrollAdapter();
            mAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mAdapter);

            mSongSection = new SongSection(this, mSongs);
           // mShuffleAllSection = new ShuffleAllSection(this, mSongs);
           // mAdapter.addSection(mShuffleAllSection);
            mAdapter.addSection(mSongSection);
            mAdapter.setEmptyState(new LibraryEmptyState(getActivity()));
        }
    }
}
