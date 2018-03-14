package com.devphill.music.ui.library.net_songs.give_pop_songs_and_radio;

import android.net.Uri;

import com.devphill.music.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by lerich on 10.03.18.
 */

public class SongsMultiple {

    ObjectTrack objectTrack = new ObjectTrack();
    List<Song> songList = new ArrayList<>();
    Uri uri;


    public List<Song> getSongs (List<ObjectTrack> objectTrackList) {

      //  try {
            for (int i = 0; i < objectTrackList.size(); i++) {
                objectTrack = objectTrackList.get(i);
                uri = Uri.parse(objectTrack.getTrackURL());
                Song song = new Song.Builder()
                        .setSongName(objectTrack.getTrackName())
                        .setSongId(i)
                        .setArtistName(objectTrack.getTrackArtist())
                        .setArtistId(i)
                        .setAlbumName(objectTrack.getTrackTime())
                        .setAlbumId(i)
                        .setLocation(uri)
                        .setSongDuration(TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES))
                        .setYear(2016)
                        .setDateAdded(System.currentTimeMillis())
                        .setTrackNumber(i)
                        .setInLibrary(true)
                        .build();
                songList.add(song);
            }

      //  } catch (Exception e) {
         //   e.printStackTrace();
       // }
        return songList;
    }

}
