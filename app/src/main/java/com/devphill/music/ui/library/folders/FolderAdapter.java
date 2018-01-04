package com.devphill.music.ui.library.folders;


import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.devphill.music.R;
import com.devphill.music.model.Folder;
import com.devphill.music.model.FolderSong;
import com.devphill.music.model.ModelUtil;
import com.devphill.music.model.Song;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>   implements FastScrollRecyclerView.SectionedAdapter {

    private static final String LOG_TAG = "FolderAdapter";

    private static Context mContext;
    private Activity myActivity;
    private List<FolderSong> mList;
    OnClickListener onClickListener;


    public FolderAdapter(Context context, Activity activity, List<FolderSong> list) {

        mContext = context;
        myActivity = activity;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case FolderSong.TYPE_FOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
                return new FolderViewHolder(view,mContext,myActivity);
            case FolderSong.TYPE_SONG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
                return new SongViewHolder(view,mContext,myActivity);
            case FolderSong.TYPE_FOLDER_UP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_up_item, parent, false);
                return new FolderUpViewHolder(view,mContext,myActivity);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (mList.get(position).getType()) {                                          //определяем тип елемента списка
            case FolderSong.TYPE_FOLDER:                                                 //если это папка
                final FolderViewHolder folderViewHolder = ((FolderViewHolder) holder);  //создаем холдер начего типа
                folderViewHolder.title.setText(mList.get(position).getFolder().getFolder_name());   //пишем имя папки
                folderViewHolder.count_songs.setText(mList.get(position).getFolder().getCount_songs() + " песен"); //пишем сколько там песен
                folderViewHolder.layout.setOnClickListener(view -> {    //слушатель нажатия на елемент списка
                    onClickListener.onFolderClickListener(position);    //передаем чепрез интерфейс в фрагмент
                });
                break;
            case FolderSong.TYPE_SONG:                                                              //если это песня
                final SongViewHolder songViewHolder = ((SongViewHolder) holder);
                songViewHolder.song_name.setText(mList.get(position).getSong().getSongName());      //пишеи имя
                songViewHolder.artist_name.setText(mList.get(position).getSong().getArtistName());  //пишем артиста

                SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("mm:ss");      //длительность из лонга
                String strTime = simpleDateFormatDate.format(new Date(mList.get(position).getSong().getSongDuration()));
                songViewHolder.duration.setText(strTime);

                songViewHolder.layout.setOnClickListener(view -> {
                    onClickListener.onSongClickListener(position);
                });
                break;
            case FolderSong.TYPE_FOLDER_UP: //если это вверх
                final FolderUpViewHolder folderUpViewHolder = ((FolderUpViewHolder) holder);
                folderUpViewHolder.layout.setOnClickListener(view -> {
                    onClickListener.onFolderUpClickListener();
                });
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            return mList.get(position).getType();
        }
        return 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {

        String res = "";
        if (mList.get(position).getType() == FolderSong.TYPE_FOLDER) {
            char firstCharF = ModelUtil.sortableTitle(mList.get(position).getFolder().getFolder_name()).charAt(0);
            res = Character.toString(firstCharF).toUpperCase();
        }
        if (mList.get(position).getType() == FolderSong.TYPE_SONG) {
            char firstCharS = ModelUtil.sortableTitle(mList.get(position).getSong().getArtistName()).charAt(0);
            res = Character.toString(firstCharS).toUpperCase();
        }

        return res;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder{

        private TextView title,count_songs;
        private View layout;

        public FolderViewHolder(View v,Context context,Activity activity) {
            super(v);
            this.title =  v.findViewById(R.id.folder_name);
            this.count_songs =  v.findViewById(R.id.count_songs);
            this.layout = v.findViewById(R.id.layout);
        }
    }

    public class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView song_name,artist_name,duration;
        private View layout;

        public SongViewHolder(View v,Context context,Activity activity) {
            super(v);
              this.song_name =  v.findViewById(R.id.song_name);
              this.artist_name =  v.findViewById(R.id.artist);
              this.duration =  v.findViewById(R.id.duration);
              this.layout = v.findViewById(R.id.layout);
        }
    }

    public class FolderUpViewHolder extends RecyclerView.ViewHolder{

        private View layout;
        private TextView parent_folder,point;


        public FolderUpViewHolder(View v,Context context,Activity activity) {
            super(v);
            this.layout = v.findViewById(R.id.layout);
            this.parent_folder =  v.findViewById(R.id.parent_folder);
            this.point =  v.findViewById(R.id.point);

            parent_folder.setText("Родительская папка");
            point.setText("...");
        }
    }

    public interface OnClickListener{

        void onFolderClickListener(int position);
        void onSongClickListener(int position);
        void onFolderUpClickListener();

    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

}
