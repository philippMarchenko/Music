package com.devphill.music.ui.library.net_songs;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.devphill.music.R;
import com.devphill.music.model.FolderSong;
import com.devphill.music.model.ModelUtil;
import com.devphill.music.model.Song;
import com.devphill.music.ui.library.folders.FolderAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

public class NetSongsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>   implements FastScrollRecyclerView.SectionedAdapter {


    private static final String LOG_TAG = "NetSongsAdapter";

    private static Context mContext;
    private Activity myActivity;
    private List<Song> mList;
    private OnClickListener onClickListener;


    public NetSongsAdapter(Context context, Activity activity, List<Song> list){

        mContext = context;
        myActivity = activity;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.net_song_item, parent, false);
        return new NetSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Song song = mList.get(position);

        NetSongViewHolder netSongViewHolder = (NetSongViewHolder) holder;

        netSongViewHolder.artist_name.setText(song.getArtistName());
        netSongViewHolder.song_name.setText(song.getSongName());

        netSongViewHolder.progressDownload.setProgress(song.getDownloadProgress());

        netSongViewHolder.download_button.setOnClickListener(view -> {
            if(!(song.getDownloadStatus() == Song.SONG_DOWNLOADING)){
                onClickListener.onDownloadClickStart(position);
            }
        });

        netSongViewHolder.layout.setOnClickListener(view -> {
            onClickListener.onSongClick(position);
        });


        netSongViewHolder.info.setOnClickListener(v -> {
            onClickListener.onClickInfo(position);
        });

        if(song.getDownloadStatus() == Song.SONG_DOWNLOADING){
            netSongViewHolder.download_button.setVisibility(View.INVISIBLE);
         //   netSongViewHolder.download_button.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cancel));
        }
        else{
            netSongViewHolder.download_button.setVisibility(View.VISIBLE);
            netSongViewHolder.download_button.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.download));
        }

        if(song.getDownloadStatus() == Song.SONG_DOWNLOADED){
            netSongViewHolder.download_button.setVisibility(View.INVISIBLE);
            netSongViewHolder.progressDownload.setProgress(100);
        }

        if(song.isPlaying()){
            netSongViewHolder.playingIndicator.setVisibility(View.VISIBLE);
        }
        else{
            netSongViewHolder.playingIndicator.setVisibility(View.INVISIBLE);
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
        return  Character.toString(firstCharF).toUpperCase();
    }

    private class NetSongViewHolder extends RecyclerView.ViewHolder{

        private TextView song_name,artist_name;
        private View layout;
        private ImageView playingIndicator,info;
        private ImageButton download_button;
        private ProgressBar progressDownload;

        public NetSongViewHolder(View v) {
            super(v);
            this.song_name =  v.findViewById(R.id.song_name);
            this.artist_name =  v.findViewById(R.id.artist_name);
            this.layout = v.findViewById(R.id.layout);
            this.playingIndicator =  v.findViewById(R.id.playingIndicator);
            this.info =  v.findViewById(R.id.info);
            this.download_button = v.findViewById(R.id.download_button);
            this.progressDownload = v.findViewById(R.id.progressDownload);
        }
    }

    public interface OnClickListener{
        void onDownloadClickStart(int position);
        void onDownloadClickStop(int position);
        void onSongClick(int position);
        void onClickInfo(int position);


    }

    public void setOnClickListenet(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
}
