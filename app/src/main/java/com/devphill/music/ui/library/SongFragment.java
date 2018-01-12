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

import com.devphill.music.data.store.SharedPreferenceStore;
import com.devphill.music.lastfm.api.LastFmApi;
import com.devphill.music.lastfm.api.LastFmService;
import com.devphill.music.lastfm.api.model.LfmArtistResponse;
import com.devphill.music.lastfm.model.Image;
import com.devphill.music.lastfm.model.LfmArtist;
import com.devphill.music.player.PlayerController;
import com.devphill.music.player.ServicePlayerController;
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
    private SongAdapter songAdapter;
    private List<Song> mSongs = new ArrayList<>();
    private List<Song> allSongs;

    private static final String LOG_TAG = "SongFragment";

    public static final String SEARCH_ALL_SONGS = "search_all_songs";
    private BroadcastReceiver br;

    private LastFmService lastFmService;
    private SimpleArrayMap<String, Observable<LfmArtist>> mCachedArtistInfo;

    int playingLastPosition;

    private PlayerController mPlayerController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library_page, container, false);
        JockeyApplication.getComponent(this).inject(this);

        mPlayerController = new ServicePlayerController(getContext(), new SharedPreferenceStore(getContext()));


        mRecyclerView = view.findViewById(R.id.library_page_list);
        mRecyclerView.addItemDecoration(new BackgroundDecoration());
        mRecyclerView.addItemDecoration(new DividerDecoration(getContext(), R.id.empty_layout));

        mRecyclerView.getItemAnimator().setChangeDuration(0);   //фикс бага мигания елемента

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        lastFmService = LastFmApi.getService(getContext());
        mCachedArtistInfo = new SimpleArrayMap<>();

        mMusicStore.getSongs()
                .compose(bindToLifecycle())
                .subscribe(
                        songs -> {
                            allSongs = songs;
                            findlogoArtist();
                            setupAdapter();
                        },
                        throwable -> Timber.e(throwable, "Failed to get new songs"));


        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String charString = intent.getCharSequenceExtra("search").toString();
                if (charString.isEmpty()) {
                    mSongs.clear();
                    mSongs.addAll(allSongs);
                    songAdapter.notifyDataSetChanged();
                } else {
                    List<Song> filteredList = new ArrayList<>();
                    for (Song row : allSongs) {
                        if (row.getSongName().toLowerCase().contains(charString.toLowerCase())
                                || row.getAlbumName().toLowerCase().contains(charString.toLowerCase())
                                || row.getArtistName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mSongs.clear();
                    mSongs.addAll(filteredList);
                    songAdapter.notifyDataSetChanged();
                }
            }
        };

        getContext().registerReceiver(br,new IntentFilter(SEARCH_ALL_SONGS));

        int paddingH = (int) getActivity().getResources().getDimension(R.dimen.global_padding);
        view.setPadding(paddingH, 0, paddingH, 0);


        return view;
    }

    private void findlogoArtist(){

        Log.d(LOG_TAG, "findlogoArtist ");

        mSongs.addAll(allSongs);

        for(int i = 0; i < mSongs.size(); i++){

         //   getArtistInfo(mSongs.get(i).getArtistName());

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
                                songAdapter.notifyItemChanged(finalI);
                            },
                            throwable -> {
                                Timber.e(throwable, "Failed to get Last.fm artist info");
                            });


        }


    }

    private void setupAdapter(){
        songAdapter = new SongAdapter(getContext(),getActivity(),mSongs);

        songAdapter.setSongAdapterListener(new SongAdapter.SongAdapterListener() {
            @Override
            public void onSongClick(int position) {
                mSongs.get(playingLastPosition).setPlaying(false);
                songAdapter.notifyItemChanged(playingLastPosition);

                //   mSongs.get(position).setPlaying(true);

                mPlayerController.setQueue(mSongs, position);
                mPlayerController.play();

               // songAdapter.notifyItemChanged(position);

                playingLastPosition = position;
            }
        });

        mRecyclerView.setAdapter(songAdapter);
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
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
      //  mAdapter = null;
     //   mSongSection = null;

        try {
            getContext().unregisterReceiver(br);
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Не удалось снять с регистрации приемник" + e.getMessage());
        }

    }

}
