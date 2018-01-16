package com.devphill.music.ui.library.net_songs;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.devphill.music.R;
import com.devphill.music.model.SongDetail;
import com.google.android.youtube.player.YouTubeBaseActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SongDetailActivity extends YouTubeBaseActivity {

    private String songUrl;

    private static final String LOG_TAG = "SongDetailActivity";

    ParseSongDetail parseSongDetail;

    TextView duration,size,quality;

    RecyclerView mRecyclerView;

    SongDetail mSongDetail;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        context = this;

        size = findViewById(R.id.size);
        duration = findViewById(R.id.duration);
        quality = findViewById(R.id.quality);

        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            songUrl = extras.getString("songUrl");
        }

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
    }
}
