package com.devphill.music.ui.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.data.store.MediaStoreUtil;
import com.devphill.music.model.Song;
import com.devphill.music.player.PlayerController;
import com.devphill.music.ui.BaseLibraryActivity;
import com.devphill.music.utils.UriUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class LibraryActivity extends BaseLibraryActivity {

    private static final String ACTION_SHOW_NOW_PLAYING_PAGE = "LibraryActivity.ShowNowPlayingPage";
    private static final String LOG_TAG = "LibraryActivity";

    @Inject PlayerController mPlayerController;

    private LibraryFragment libraryFragment;

    public static Intent newNowPlayingIntent(Context context) {
        Intent intent = new Intent(context, LibraryActivity.class);
        intent.setAction(ACTION_SHOW_NOW_PLAYING_PAGE);
        return intent;
    }

    @Override
    protected Fragment onCreateFragment(Bundle savedInstanceState) {

        libraryFragment = LibraryFragment.newInstance();
        return libraryFragment;
    }

    @Override
    public void onBackPressed() {

     if(LibraryPagerAdapter.state_net_song_fragment == 1){

            libraryFragment.updateNetSongsFragment();
            Log.d(LOG_TAG,"onBackPressed LibraryPagerAdapter 1 " );


     } else{
            super.onBackPressed();
        }

        Log.d(LOG_TAG,"onBackPressed " );

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JockeyApplication.getComponent(this).inject(this);
        onNewIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent.getAction() == null) {
            return;
        }

        if (intent.getAction().equals(ACTION_SHOW_NOW_PLAYING_PAGE)) {
            expandBottomSheet();
            // Don't try to process this intent again
            setIntent(new Intent(this, LibraryActivity.class));
            return;
        }

        // Handle incoming requests to play media from other applications
        if (intent.getData() == null) {
            return;
        }

        // If this intent is a music intent, process it
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            MediaStoreUtil.promptPermission(this)
                    .subscribe(hasPermission -> {
                        if (hasPermission) {
                            startPlaybackFromUri(intent.getData());
                        }
                    }, throwable -> {
                        Timber.e(throwable, "Failed to start playback from URI %s",
                                intent.getData());
                    });
        }

        // Don't try to process this intent again
        setIntent(new Intent(this, LibraryActivity.class));
    }

    private void startPlaybackFromUri(Uri songUri) {
        String songName = UriUtils.getDisplayName(this, songUri);

        List<Song> queue = buildQueueFromFileUri(songUri);
        int position;

        if (queue == null || queue.isEmpty()) {
            queue = buildQueueFromUri(songUri);
            position = findStartingPositionInQueue(songUri, queue);
        } else {
            String path = UriUtils.getPathFromUri(this, songUri);
            //noinspection ConstantConditions This won't be null, because we found data from it
            Uri fileUri = Uri.fromFile(new File(path));
            position = findStartingPositionInQueue(fileUri, queue);
        }

        if (queue.isEmpty()) {
            showSnackbar(getString(R.string.message_play_error_not_found, songName));
        } else {
            startIntentQueue(queue, position);
        }

        expandBottomSheet();
    }

    private List<Song> buildQueueFromFileUri(Uri fileUri) {
        // URI is not a file URI
        String path = UriUtils.getPathFromUri(this, fileUri);
        if (path == null || path.trim().isEmpty()) {
            return Collections.emptyList();
        }

        File file = new File(path);
        String mimeType = getContentResolver().getType(fileUri);
        return MediaStoreUtil.buildSongListFromFile(this, file, mimeType);
    }

    private List<Song> buildQueueFromUri(Uri uri) {
        return Collections.singletonList(Song.fromUri(this, uri));
    }

    private int findStartingPositionInQueue(Uri originalUri, List<Song> queue) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getLocation().equals(originalUri)) {
                return i;
            }
        }

        return 0;
    }

    private void startIntentQueue(List<Song> queue, int position) {
        mPlayerController.setQueue(queue, position);
        mPlayerController.play();
    }

    @Override
    protected int getSnackbarContainerViewId() {
        return R.id.library_pager;
    }

}
