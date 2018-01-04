package com.devphill.music.ui.library.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.devphill.music.model.Playlist;
import com.devphill.music.ui.BaseLibraryActivity;

public class PlaylistActivity extends BaseLibraryActivity {

    private static final String PLAYLIST_EXTRA = "PlaylistActivity.PLAYLIST";

    public static Intent newIntent(Context context, Playlist playlist) {
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra(PLAYLIST_EXTRA, playlist);

        return intent;
    }

    @Override
    protected Fragment onCreateFragment(Bundle savedInstanceState) {
        Playlist playlist = getIntent().getParcelableExtra(PLAYLIST_EXTRA);
        return PlaylistFragment.newInstance(playlist);
    }

}
