package com.devphill.music.ui.library.net_songs;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.youtube.player.YouTubeBaseActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SongDetailFragment extends BaseFragment {

    private String songUrl;

    private static final String LOG_TAG = "SongDetailFragment";

    ParseSongDetail parseSongDetail;

    TextView duration,size,quality;

    RecyclerView mRecyclerView;

    SongDetail mSongDetail;

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_song_detail, container, false);

      //  context = this;

        size = view.findViewById(R.id.size);
        duration = view.findViewById(R.id.duration);
        quality = view.findViewById(R.id.quality);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


       /* Bundle extras = getIntent().getExtras();
        if (extras != null) {
            songUrl = extras.getString("songUrl");
        }*/

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

                mSongDetail = songDetail;

                SongDetailAdapter songDetailAdapter = new SongDetailAdapter(context,mSongDetail);
                mRecyclerView.setAdapter(songDetailAdapter);

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
