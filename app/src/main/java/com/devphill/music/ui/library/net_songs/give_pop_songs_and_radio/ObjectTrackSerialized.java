package com.devphill.music.ui.library.net_songs.give_pop_songs_and_radio;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lerich on 12.02.18.
 */

public class ObjectTrackSerialized {

    @SerializedName("Tracks")
    @Expose
    private List tracks = null;

    ObjectTrackSerialized (List tracks){

        this.tracks = tracks;

    }


    public List getTracks() {
        return tracks;
    }

    public void setTracks(List tracks) {
        this.tracks = tracks;
    }

}


