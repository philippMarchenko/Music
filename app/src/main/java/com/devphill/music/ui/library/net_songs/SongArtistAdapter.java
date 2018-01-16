package com.devphill.music.ui.library.net_songs;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devphill.music.R;
import com.devphill.music.data.store.SharedPreferenceStore;
import com.devphill.music.model.ArtistSong;
import com.devphill.music.model.Song;
import com.devphill.music.player.PlayerController;
import com.devphill.music.player.ServicePlayerController;

import org.w3c.dom.Text;

import java.util.List;

public class SongArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Song> artistSongList;
    private PlayerController mPlayerController;


    SongArtistAdapter(List<Song> artistSongList, Context context){

        this.artistSongList = artistSongList;
        mPlayerController = new ServicePlayerController(context, new SharedPreferenceStore(context));

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SongViewHolder songViewHolder = (SongViewHolder) holder;

        songViewHolder.artist.setText(artistSongList.get(position).getArtistName());
        songViewHolder.song_name.setText(artistSongList.get(position).getSongName());
      //  songViewHolder.duration.setText(artistSongList.get(position).getSongDuration());

        songViewHolder.layout.setOnClickListener(view -> {
            mPlayerController.setQueue(artistSongList, position);
            mPlayerController.play();
        });

    }

    @Override
    public int getItemCount() {
        return artistSongList.size();
    }

    private class SongViewHolder extends RecyclerView.ViewHolder{

        TextView song_name,artist,duration;
        View layout;

        public SongViewHolder(View itemView) {
            super(itemView);

            duration = itemView.findViewById(R.id.duration);
            artist = itemView.findViewById(R.id.artist);
            song_name = itemView.findViewById(R.id.song_name);
            layout = itemView.findViewById(R.id.layout);


        }
    }
}
