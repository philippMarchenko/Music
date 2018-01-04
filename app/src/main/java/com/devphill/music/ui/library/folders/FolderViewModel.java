package com.devphill.music.ui.library.folders;


import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.devphill.music.BR;
import com.devphill.music.data.store.MediaStoreUtil;
import com.devphill.music.model.Song;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

public class FolderViewModel extends BaseObservable {

    public static final String LOG_TAG = "FolderViewModel";

    private Context mContext;
    private Activity mActivity;

    private File mReference;

    FolderAdapterListener mFolderAdapterListener;

    public interface FolderAdapterListener{

        void onFolderClickListener(List<File> fileList, List<Song> songList,String dir);

    }



    public FolderViewModel(List<File> folders,Context context,FolderAdapterListener folderAdapterListener) {

        mContext = context;
        mFolderAdapterListener = folderAdapterListener;
       // mActivity = activity;

    }


    public View.OnClickListener onClickFolder() {
        return v -> {
         //   Toast.makeText(mContext, "onClickFolder", Toast.LENGTH_LONG).show();
            Log.d("FolderViewModel", "onClickFolder " );
            Log.d("FolderViewModel", "getAbsolutePath " + mReference.getAbsolutePath() );

            File[] directories = new File(mReference.getAbsolutePath()).listFiles(file -> file.isDirectory());
            List<File> dirList = Arrays.asList(directories);

            String selections = MediaStoreUtil.getDirectoryInclusionExclusionSelection(mReference.getAbsolutePath());

            List<Song> songList =  MediaStoreUtil.getSongs(mContext,selections, null);

            mFolderAdapterListener.onFolderClickListener(dirList,songList,mReference.getAbsolutePath());
        };
    }

    public void setFolder(List<File> folders,int index){

         mReference = folders.get(index);
         Log.d(LOG_TAG, "setFolder " + folders.get(index));
         Log.d(LOG_TAG, "index " + index);

         notifyPropertyChanged(BR.title);

    }

    @Bindable
    public String getTitle() {
        return mReference.getName();
    }
}
