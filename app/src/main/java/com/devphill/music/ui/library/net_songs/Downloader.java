package com.devphill.music.ui.library.net_songs;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class Downloader {

    public static final String LOG_TAG = "Downloader";

    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;

    public static File folder  = new File(Environment.getExternalStorageDirectory() +
            File.separator + "MusicAppDownload");

    Context context;

    ThinDownloadManager downloadManager;

    public Downloader (Context context) {
        this.context = context;

        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);


    }


    public Observable downloadFile(final String url ,String name) {

        Observable downloadObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> emitter) throws Exception {
                try{

                    Log.d(LOG_TAG,"init download... ");
                    Log.d(LOG_TAG,"url " + url);
                    Log.d(LOG_TAG,"name " + name);

                    Uri downloadUri = Uri.parse(url);
                    Uri destinationUri = Uri.parse(folder.getAbsolutePath() + "/" + name + ".mp3");
                    DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
                            .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
                            .setRetryPolicy(new DefaultRetryPolicy())
                            .setDownloadContext("Download1")
                            .setStatusListener(new DownloadStatusListenerV1() {
                                @Override
                                public void onDownloadComplete(DownloadRequest downloadRequest) {
                                    emitter.onComplete();
                                    refreshGallery(folder.getAbsolutePath() + "/" + name + ".mp3");
                                    Log.d(LOG_TAG, "onDownloadComplete  ");
                                }

                                @Override
                                public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                                    Log.d(LOG_TAG, "onDownloadFailed  " + errorMessage);
                                }

                                @Override
                                public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                                    emitter.onNext(progress);
                                    Log.d(LOG_TAG, "onProgress  " + progress);

                                }
                            });

                    downloadManager.add(downloadRequest1);
                }
                catch (Exception e ){
                    Log.d(LOG_TAG,"error init download  " + e.getMessage());
                    emitter.onError(e);
                }
            }
        });

        return downloadObservable;
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
                context.sendBroadcast(mediaScanIntent);
            } else {
                // Delete File
                try {
                    context.getContentResolver().delete(
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

    public void createMyFolder(){


        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
    }

    public void release(){
        downloadManager.release();

    }

}