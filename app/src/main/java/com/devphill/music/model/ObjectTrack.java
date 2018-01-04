package com.devphill.music.model;

/**
 * Created by Lerich on 22.11.2017.
 */

public class ObjectTrack extends Song {
    private String trackFullName, trackName, trackArtist, trackURL, trackTime;

    public ObjectTrack() {

    }

    public ObjectTrack (Song song) {
       song = new Song();
    }

    public ObjectTrack(String trackFullName,
                       String trackTime,
                       String trackURL) {
        this.trackFullName = trackFullName;
        this.trackTime = trackTime;
        this.trackURL = trackURL;
    }

    public ObjectTrack(String trackName,
                       String trackArtist,
                       String trackTime,
                       String trackURL) {
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.trackTime = trackTime;
        this.trackURL = trackURL;
    }

    public String getTrackFullName() {
        return trackFullName;
    }

    public void setTrackFullName() {
        this.trackFullName = trackFullName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName() {
        this.trackName = trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist() {
        this.trackArtist = trackArtist;
    }

    public String getTrackTime() {
        return trackTime;
    }

    public void setTrackTime() {
        this.trackTime = trackTime;
    }

    public String getTrackURL() {
        return trackURL;
    }

    public void setTrackURL() {
        this.trackURL = trackURL;
    }
}
