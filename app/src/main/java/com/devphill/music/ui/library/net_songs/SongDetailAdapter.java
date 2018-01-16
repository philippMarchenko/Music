package com.devphill.music.ui.library.net_songs;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devphill.music.R;
import com.devphill.music.model.SongDetail;
import com.devphill.music.view.BackgroundDecoration;
import com.devphill.music.view.DividerDecoration;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.silencedut.expandablelayout.ExpandableLayout;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.HashSet;

public class SongDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private LayoutInflater mInflater;
    private HashSet<Integer> mExpandedPositionSet = new HashSet<>();

    private static final String LOG_TAG = "SongDetailAdapter";

    private SongDetail songDetail;
    private Context mContext;


    public SongDetailAdapter(Context context, SongDetail songDetail) {
        this.mInflater = LayoutInflater.from(context);
        this.songDetail = songDetail;
        mContext = context;

        Log.d(LOG_TAG,"getVideoUrl " + songDetail.getVideoUrl());
        Log.d(LOG_TAG,"getVideoUrl " + songDetail.getVideoUrl().substring(24, 35));

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;

        if(viewType == 0){
            item = mInflater.inflate(R.layout.item_song_detail_video,parent,false);
            return new SongVideoViewHolder(item);
        }
        else{
            item = mInflater.inflate(R.layout.item_song_detail_list,parent,false);
            return new SongDetailViewHolder(item);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position%2;
    }

    public class SongVideoViewHolder  extends RecyclerView.ViewHolder{

        private ExpandableLayout expandableLayout;
        private YouTubePlayerView youTubeView;

        public SongVideoViewHolder(View itemView) {
            super(itemView);


       /*     YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                    youTubePlayer.cueVideo(songDetail.getVideoUrl().substring(24, 35));
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Log.d(LOG_TAG,"onInitializationFailure ");

                }
            };

            youTubeView = (YouTubePlayerView) itemView.findViewById(R.id.youtube_view);
            youTubeView.initialize("AIzaSyAW4zFM9keH8D0uDd3YGbysra3Ci8Sn-tM", onInitializedListener);*/

            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);

        }
        private void updateItem(final int position) {
            expandableLayout.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
                @Override
                public void onExpand(boolean expanded) {
                    registerExpand(position);
                }
            });
            expandableLayout.setExpand(mExpandedPositionSet.contains(position));

        }
    }

    public class SongDetailViewHolder extends RecyclerView.ViewHolder{

        private ExpandableLayout expandableLayout ;
        private FastScrollRecyclerView mRecyclerView;

        public SongDetailViewHolder(View itemView) {
            super(itemView);
            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);

            mRecyclerView = itemView.findViewById(R.id.library_page_list);
            mRecyclerView.addItemDecoration(new BackgroundDecoration());
          //  mRecyclerView.addItemDecoration(new DividerDecoration(getContext(), R.id.empty_layout));

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);

            SongArtistAdapter songArtistAdapter = new SongArtistAdapter(songDetail.getArtistSongList(),mContext);
            mRecyclerView.setAdapter(songArtistAdapter);

        }

        public void updateItem(final int position) {
            expandableLayout.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
                @Override
                public void onExpand(boolean expanded) {
                    registerExpand(position);
                }
            });
            expandableLayout.setExpand(mExpandedPositionSet.contains(position));

        }
    }

    private void registerExpand(int position) {
        if (mExpandedPositionSet.contains(position)) {
            removeExpand(position);
        }else {
            addExpand(position);
        }
    }

    private void removeExpand(int position) {
        mExpandedPositionSet.remove(position);
    }

    private void addExpand(int position) {
        mExpandedPositionSet.add(position);
    }

}
