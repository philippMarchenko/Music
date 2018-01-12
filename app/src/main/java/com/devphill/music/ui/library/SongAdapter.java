package com.devphill.music.ui.library;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.devphill.music.R;
import com.devphill.music.model.ModelUtil;
import com.devphill.music.model.Song;
import com.devphill.music.ui.library.net_songs.NetSongsAdapter;
import com.devphill.music.view.ViewUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

public class SongAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>   implements FastScrollRecyclerView.SectionedAdapter{


    private static final String LOG_TAG = "NetSongsAdapter";

    private static Context mContext;
    private Activity myActivity;
    private List<Song> mList;
    private SongAdapterListener songAdapterListener;

    public interface SongAdapterListener{
        void onSongClick(int position);
    }

    public void setSongAdapterListener(SongAdapterListener songAdapterListener){
        this.songAdapterListener = songAdapterListener;
    }
    public SongAdapter(Context context, Activity activity, List<Song> list){

        mContext = context;
        myActivity = activity;
        mList = list;
    }

    private class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView song_name,artist_name;
        private View layout;
        private ImageView playingIndicator,artist_art;

        public SongViewHolder(View v) {
            super(v);
            this.song_name =  v.findViewById(R.id.song_name);
            this.artist_name =  v.findViewById(R.id.artist_name);
            this.layout = v.findViewById(R.id.layout);
            this.playingIndicator =  v.findViewById(R.id.playingIndicator);
            this.artist_art =  v.findViewById(R.id.artist_art);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Song song = mList.get(position);
        SongViewHolder songViewHolder = (SongViewHolder) holder;

        songViewHolder.artist_name.setText(song.getArtistName());
        songViewHolder.song_name.setText(song.getSongName());

        songViewHolder.layout.setOnClickListener(view -> {
            songAdapterListener.onSongClick(position);
            songViewHolder.playingIndicator.setVisibility(View.VISIBLE);
        });

        try{
            if(!song.getArtistImageUrl().isEmpty()){
                Glide.with(mContext)
                        .load(song.getArtistImageUrl())
                        .into(songViewHolder.artist_art);
            }
            else {
                songViewHolder.artist_art.setImageDrawable(mContext.getResources().getDrawable(R.drawable.art_default));
            }
        }
        catch (Exception e){

        }

        if(!song.isPlaying()){
            songViewHolder.playingIndicator.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        char firstCharF = ModelUtil.sortableTitle(mList.get(position).getArtistName()).charAt(0);
        return  Character.toString(firstCharF).toUpperCase();    }
}
