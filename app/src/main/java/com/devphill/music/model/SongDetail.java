package com.devphill.music.model;

import java.util.List;

/**
 * Created by Филипп on 13.01.2018.
 */

public class SongDetail {

    private String bitRate;
    private String duration;
    private String size;
    private String videoUrl;
    private List<ArtistSong> artistSongList;

    public SongDetail() {

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

    public List<ArtistSong> getArtistSongList() {
        return artistSongList;
    }

    public void setArtistSongList(List<ArtistSong> artistSongList) {
        this.artistSongList = artistSongList;
    }


}
