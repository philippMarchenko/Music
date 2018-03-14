package com.devphill.music.ui.library.net_songs.give_pop_songs_and_radio;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lerich on 12.02.18.
 */

public class ServerResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Tracks")
    @Expose
    private List<ObjectTrack> tracks;

    @SerializedName("Radio")
    @Expose
    private List<ObjectRadio> radios;



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ObjectTrack> getTracks() {
        return tracks;
    }

    public void setTracks (List<ObjectTrack> tracks) {
        this.tracks = tracks;
    }

    public List<ObjectRadio> getRadios() {
        return radios;
    }

    public void setRadios (List<ObjectRadio> radios) {
        this.radios = radios;
    }
}
