package com.devphill.music.ui.library.net_songs;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.devphill.music.R;
import com.devphill.music.model.SongDetail;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SongDetailActivity extends Activity {

    private String songUrl;

    private static final String LOG_TAG = "SongDetailActivity";

    ParseSongDetail parseSongDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

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

                Log.d(LOG_TAG, "size" + songDetail.getArtistSongList().size());


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
