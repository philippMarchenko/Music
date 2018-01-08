package com.devphill.music.ui.library.folders;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devphill.music.R;
import com.devphill.music.data.store.MediaStoreUtil;
import com.devphill.music.data.store.SharedPreferenceStore;
import com.devphill.music.model.Folder;
import com.devphill.music.model.FolderSong;
import com.devphill.music.model.Song;
import com.devphill.music.player.PlayerController;
import com.devphill.music.player.ServicePlayerController;
import com.devphill.music.ui.common.LibraryEmptyState;
import com.devphill.music.ui.common.ShuffleAllSection;
import com.devphill.music.ui.library.SongSection;
import com.devphill.music.ui.library.net_songs.Downloader;
import com.devphill.music.view.BackgroundDecoration;
import com.devphill.music.view.DividerDecoration;
import com.devphill.music.view.HeterogeneousFastScrollAdapter;
import com.marverenic.adapter.HeterogeneousAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.devphill.music.ui.BaseFragment;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FoldersFragment extends BaseFragment{

   // private RecyclerView mRecyclerView;
    private FastScrollRecyclerView mRecyclerView;
    private ProgressBar progress_bar_folders;

    public static final String LOG_TAG = "FoldersFragment";


    private static final String KEY_DIRECTORY = "Directory";

    private File mDirectory;
    private List<File> mSubDirectories;
    private List<String> mSubDirectoryNames = new ArrayList<>();
    private List<Folder> mFolderList = new ArrayList<>();
    private List<FolderSong> folderSongs = new ArrayList<>();

    private List<Song> currentSongList = new ArrayList<>();
    private List<File> currentFolderList = new ArrayList<>();


    private ShuffleAllSection mShuffleAllSection;
    private SongSection mSongSection;
    FoldersSection foldersSection;

    private String prevDir;

    FolderAdapter folderAdapter;
    PlayerController mPlayerController;

    private boolean firstShowList = false;                            //первый раз показываем список
    private int countFolders = 0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(KEY_DIRECTORY);
            if (path != null) {
                mDirectory = new File(path);
            }
        }

        if (mDirectory == null) {
            mDirectory = Environment.getExternalStorageDirectory();
        }
        Log.i(LOG_TAG, "onCreate " );

                                                                                  //  scanSubDirs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        mPlayerController = new ServicePlayerController(getContext(), new SharedPreferenceStore(getContext()));


        mRecyclerView = view.findViewById(R.id.list_folders_songs);
        progress_bar_folders = view.findViewById(R.id.progress_bar_folders);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        folderAdapter = new FolderAdapter(getContext(),getActivity(),folderSongs);
        mRecyclerView.setAdapter(folderAdapter);

        setOnclickListener();

        int paddingH = (int) getActivity().getResources().getDimension(R.dimen.global_padding);
        view.setPadding(paddingH, 0, paddingH, 0);

        getFoldersSongs(mDirectory.getAbsolutePath())
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeWith(new DisposableObserver() {

                     @Override
                     public void onNext(Object value) {
                         Log.d(LOG_TAG,"onError getFoldersSongs " );
                     }
                     @Override
                     public void onError(Throwable e) {
                         Log.d(LOG_TAG,"onError getFoldersSongs " + e.getMessage());
                     }
                     @Override
                     public void onComplete() {
                         Log.d(LOG_TAG,"onComplete getFoldersSongs ");
                         try {
                             progress_bar_folders.setVisibility(View.INVISIBLE);
                             folderAdapter.notifyDataSetChanged();
                         }
                         catch (Exception e){

                         }
                     }
                 });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        folderAdapter = null;
        folderSongs.clear();

    }

    void setOnclickListener() {

        folderAdapter.setOnClickListener(new FolderAdapter.OnClickListener() {
            @Override
            public void onFolderClickListener(int position) {
                onClickFolder(position);
            }

            @Override
            public void onSongClickListener(int position) {
                onClickSong(position);
            }

            @Override
            public void onFolderUpClickListener() {
                onFolderUp();
            }
        });


    }

    private void onClickFolder(int position){   //нажали на папку


        folderSongs.clear();        //очищаем масив обьектов папок и песен
        mRecyclerView.setVisibility(View.INVISIBLE);
        progress_bar_folders.setVisibility(View.VISIBLE);

        int mPosition = position;

        if(firstShowList){      //если мы внутри какой то папки то сдвинем позицию на один вниз, компенсируя елемент "Вверх"
            mPosition = position;
        }

        refreshGallery(currentFolderList.get(mPosition).getAbsolutePath());


        getFoldersSongs(currentFolderList.get(mPosition)
                .getAbsolutePath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver() {

                    @Override
                    public void onNext(Object value) {
                        Log.d(LOG_TAG,"onError getFoldersSongs " );

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(LOG_TAG,"onError getFoldersSongs " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"onComplete getFoldersSongs ");
                        mRecyclerView.setVisibility(View.VISIBLE);
                        progress_bar_folders.setVisibility(View.INVISIBLE);
                        folderAdapter.notifyDataSetChanged();
                        firstShowList = true;           //флаг что список уже на экране

                        mRecyclerView.post(() -> {mRecyclerView.smoothScrollToPosition(0);});
                    }
                });


        mDirectory = new File(currentFolderList.get(mPosition).getAbsolutePath());   //текущая папка
    }

    private void onClickSong(int position){

        Log.d(LOG_TAG, "onClickSong ");
        int mPosition;

        if(!firstShowList){ //если это корневая папка то нету елемента вверх
             mPosition = position - countFolders;       //позиция кликнутой песни
        }
        else{
             mPosition = position - countFolders - 1;    //позиция клмкнутой песни
        }
                                                                    //общая позиция в списке минус сколько папок минус вверх
        mPlayerController.setQueue(currentSongList, mPosition);
        mPlayerController.play();
    }

    private void onFolderUp(){

        mRecyclerView.setVisibility(View.INVISIBLE);
        progress_bar_folders.setVisibility(View.VISIBLE);
        folderSongs.clear();        //очищаем масив обьектов папок и песен

        mDirectory = new File(mDirectory.getParent());   //верхняя(родительская) папка

        getFoldersSongs(mDirectory.getAbsolutePath()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver() {

                    @Override
                    public void onNext(Object value) {
                        Log.d(LOG_TAG,"onError getFoldersSongs " );

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(LOG_TAG,"onError getFoldersSongs " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"onComplete getFoldersSongs ");
                        mRecyclerView.setVisibility(View.VISIBLE);
                        progress_bar_folders.setVisibility(View.INVISIBLE);
                        folderAdapter.notifyDataSetChanged();

                        mRecyclerView.post(() -> {mRecyclerView.smoothScrollToPosition(0);});
                    }
                });
    }

    public Observable getFoldersSongs(final String path) {

        countFolders = 0;

        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter emitter) throws Exception {
                try{
                    File[] directories = new File(path).listFiles(file -> file.isDirectory());
                    currentFolderList = Arrays.asList(directories);    //список подпапок выбраной папки
                    Collections.sort(currentFolderList, (f1, f2) -> f1.getAbsolutePath().compareToIgnoreCase(f2.getAbsolutePath()));    //отсортируем

                    String selections = MediaStoreUtil.getDirectoryInclusionExclusionSelection(path);
                    currentSongList = MediaStoreUtil.getSongs(getContext(),selections, null);   //список песен там

                    if(!mDirectory.equals(Environment.getExternalStorageDirectory())){                       //если это еще не самая верхняя папка
                        folderSongs.add(new FolderSong(FolderSong.TYPE_FOLDER_UP));                          //добавим позицию вверх переход
                    }
                    else{

                        firstShowList = false;
                    }

                    for(int i = 0; i < currentFolderList.size(); i++){        //перебираем отсканированые папки и добавляем в масив

                        String selectionsF = MediaStoreUtil.getDirectoryInclusionExclusionSelection(currentFolderList.get(i).getAbsolutePath());
                        List<Song> songList =  MediaStoreUtil.getSongs(getContext(),selectionsF, null);  //список песен там


                        Folder folder = new Folder(currentFolderList.get(i).getAbsolutePath(),currentFolderList.get(i).getName(),songList.size(),i);

                        if(songList.size() > 0){
                            folderSongs.add(new FolderSong(folder,FolderSong.TYPE_FOLDER)); //добавили все папки
                            countFolders++;
                        }
                    }

                    for(int i = 0; i < currentSongList.size(); i++){
                        folderSongs.add(new FolderSong(currentSongList.get(i),FolderSong.TYPE_SONG));   //добавили все песни
                    }
                    emitter.onComplete();
                }
                catch (Exception e ){
                    Log.d(LOG_TAG,"error  " + e.getMessage());

                    emitter.onError(e);
                }
            }
        });

        return observable;
    }

    public void refreshGallery(String fileUri) {

        // Convert to file Object
        File file = new File(fileUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Write Kitkat version specific code for add entry to gallery database
            // Check for file existence
            if (file.exists()) {
                // Add / Move File
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(new File(fileUri));
                mediaScanIntent.setData(contentUri);
                getContext().sendBroadcast(mediaScanIntent);
            } else {
                // Delete File
                try {
                    getContext().getContentResolver().delete(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media.DATA + "='"
                                    + new File(fileUri).getPath() + "'", null);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        } else {
            /*context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + context.getBaseFolder().getAbsolutePath())));*/
        }
    }


}
