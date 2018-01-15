package com.devphill.music.model;

public class ArtistSong{

    private String name;
    private String artist;
    private String duration;

    public ArtistSong(String name, String artist, String duration) {
        this.name = name;
        this.artist = artist;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}