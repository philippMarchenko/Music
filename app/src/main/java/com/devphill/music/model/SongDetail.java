package com.devphill.music.model;

import java.util.List;


public class SongDetail {

    private String bitRate;
    private String duration;
    private String size;
    private String videoUrl;
    private List<Song> artistSongList;
    private String songTitle;
    private String songArtist;

    public SongDetail() {

    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }


    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<Song> getArtistSongList() {
        return artistSongList;
    }

    public void setArtistSongList(List<Song> artistSongList) {
        this.artistSongList = artistSongList;
    }


}
