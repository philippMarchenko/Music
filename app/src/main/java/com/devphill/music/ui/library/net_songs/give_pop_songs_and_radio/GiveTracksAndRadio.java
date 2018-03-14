package com.devphill.music.ui.library.net_songs.give_pop_songs_and_radio;

import android.util.Log;

import com.devphill.music.model.Song;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lerich on 05.03.18.
 */

public class GiveTracksAndRadio {
    private Retrofit retrofit;
    private ObjectTrackAPI objectTrackAPI;

    private String s = null;
    private List<ObjectTrack> tracksArray = new ArrayList<ObjectTrack>();
    private List<ObjectRadio> radiosArray = new ArrayList<ObjectRadio>();

    private  boolean isGived = false;

    public interface GiveTracksAndRadioListener{
        void trackCompeted(List<ObjectTrack> tracksArray);
    }

    private GiveTracksAndRadioListener giveTracksAndRadioListener ;

    public void setGiveTracksAndRadioListener(GiveTracksAndRadioListener giveTracksAndRadioListener){
        this.giveTracksAndRadioListener = giveTracksAndRadioListener;
    }

    public GiveTracksAndRadio(GiveTracksAndRadioListener giveTracksAndRadioListener){
        this.giveTracksAndRadioListener = giveTracksAndRadioListener;
    }

    public void give () {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(ObjectTrackAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        objectTrackAPI = retrofit.create(ObjectTrackAPI.class);

        ObjectTrackSerialized objectTrackSerialized = new ObjectTrackSerialized(null);

        Call<ServerResponse> call = objectTrackAPI.login(objectTrackSerialized);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
              //  s = response.body().getMessage();
                tracksArray = response.body().getTracks();
                radiosArray = response.body().getRadios();

                giveTracksAndRadioListener.trackCompeted(tracksArray);


                isGived = true;
                Log.e("Response", "Complete = " + tracksArray.size() + tracksArray.get(5).getTrackName());
                Log.e("Response", "Complete = " + radiosArray.size() + radiosArray.get(5).getRadioName());

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("Response", "Fail = " + t.getMessage());
            }
        });


    }

    public List getTracksArray() {
     //   if (isGived != true) give();
        SongsMultiple songsMultiple = new SongsMultiple();
        List<Song> tracksArrays = songsMultiple.getSongs(tracksArray);
        Log.e("Response", tracksArrays.get(5).getSongName());
        return tracksArrays;
    }

    public List<ObjectRadio> getRadiosArray() {
        if (isGived != true) give();
        return radiosArray;
    }
}


