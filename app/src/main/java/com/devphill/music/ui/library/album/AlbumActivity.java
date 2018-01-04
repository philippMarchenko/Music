package com.devphill.music.ui.library.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.devphill.music.model.Album;
import com.devphill.music.ui.BaseLibraryActivity;

public class AlbumActivity extends BaseLibraryActivity {

    private static final String ALBUM_EXTRA = "AlbumActivity.ALBUM";

    public static Intent newIntent(Context context, Album album) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra(ALBUM_EXTRA, album);

        return intent;
    }

    @Override
    protected Fragment onCreateFragment(Bundle savedInstanceState) {
        Album album = getIntent().getParcelableExtra(ALBUM_EXTRA);
        return AlbumFragment.newInstance(album);
    }

    @Override
    public boolean isToolbarCollapsing() {
        return true;
    }

}
