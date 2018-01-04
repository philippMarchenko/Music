package com.devphill.music.model;


public class FolderSong {

    public final static int TYPE_FOLDER = 0;
    public final static int TYPE_SONG = 1;
    public final static int TYPE_FOLDER_UP = 2;


    private Folder folder;
    private Song song;
    private int type;

    public FolderSong(Folder folder,int type) {
        this.folder = folder;
        this.type = type;
    }
    public FolderSong(Song song,int type) {
        this.song = song;
        this.type = type;

    }
    public FolderSong(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

}
