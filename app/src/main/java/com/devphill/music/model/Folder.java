package com.devphill.music.model;


import java.util.List;

public class Folder {

    private String folder_dir;
    private String folder_name;
    private List<String> includedList;
    private int count_songs;
    private int id;


    public Folder(String folder_dir, String folder_name,int count_songs,int id) {
        this.folder_dir = folder_dir;
        this.folder_name = folder_name;
        this.count_songs = count_songs;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getCount_songs() {
        return count_songs;
    }

    public void setCount_songs(int count_songs) {
        this.count_songs = count_songs;
    }

    public List<String> getIncludedList() {
        return includedList;
    }

    public void setIncludedList(List<String> includedList) {
        this.includedList = includedList;
    }

    public String getFolder_dir() {
        return folder_dir;
    }

    public void setFolder_dir(String folder_dir) {
        this.folder_dir = folder_dir;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }
}
