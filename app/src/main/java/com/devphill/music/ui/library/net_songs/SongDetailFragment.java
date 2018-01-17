package com.devphill.music.ui.library.net_songs;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devphill.music.R;
import com.devphill.music.model.SongDetail;
import com.devphill.music.ui.BaseFragment;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SongDetailFragment extends BaseFragment {

    private String songUrl;
    private String videoUrl;

    private static final String LOG_TAG = "SongDetailFragment";

    ParseSongDetail parseSongDetail;

    TextView duration,size,quality;

    RecyclerView mRecyclerView;

    SongDetail mSongDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_detail, container, false);

        size = view.findViewById(R.id.size);
        duration = view.findViewById(R.id.duration);
        quality = view.findViewById(R.id.quality);

       // mRecyclerView = view.findViewById(R.id.recyclerView);
       // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
       // mRecyclerView.setLayoutManager(linearLayoutManager);


        songUrl = getArguments().getString("songUrl");


        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                youTubePlayer.cueVideo(videoUrl.substring(24, 35));
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(LOG_TAG,"onInitializationFailure ");

            }
        };

        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fl_youtube, youTubePlayerFragment).commit();


        Log.d(LOG_TAG,"songUrl " + songUrl);

        parseSongDetail = new ParseSongDetail();

        Observer<SongDetail> observerSongDetail = new Observer<SongDetail>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SongDetail songDetail) {

                Log.d(LOG_TAG, "size " + songDetail.getArtistSongList().size());

                size.setText(songDetail.getSize());
                duration.setText(songDetail.getDuration());
                quality.setText(songDetail.getBitRate());

                videoUrl = songDetail.getVideoUrl();

                youTubePlayerFragment.initialize("AIzaSyAW4zFM9keH8D0uDd3YGbysra3Ci8Sn-tM", onInitializedListener);


                //  mSongDetail = songDetail;

                //SongDetailAdapter songDetailAdapter = new SongDetailAdapter(getContext(),mSongDetail,getFragmentManager());
               // mRecyclerView.setAdapter(songDetailAdapter);

            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };

        parseSongDetail.getSongDetail(songUrl).subscribe(observerSongDetail);

        //parseSongDetail.get(songUrl);
        return view;
    }
}
