package com.devphill.music.ui.library.net_songs.give_pop_songs_and_radio;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by lerich on 05.03.18.
 */

public interface ObjectTrackAPI {


        public static final String BASE_URL = "http://lerich.mkdeveloper.ru/musicPlayer/";

        @POST("giveListTrackAndRadio.php")
        Call<ServerResponse> login(@Body ObjectTrackSerialized objectTrackSerialized);

}
