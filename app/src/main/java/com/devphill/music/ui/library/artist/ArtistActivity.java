package com.devphill.music.ui.library.artist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.devphill.music.model.Artist;
import com.devphill.music.ui.BaseLibraryActivity;

public class ArtistActivity extends BaseLibraryActivity {

    private static final String ARTIST_EXTRA = "ArtistActivity.ARTIST";

    public static Intent newIntent(Context context, Artist artist) {
        Intent intent = new Intent(context, ArtistActivity.class);
        intent.putExtra(ARTIST_EXTRA, artist);

        return intent;
    }

    @Override
    protected Fragment onCreateFragment(Bundle savedInstanceState) {
        Artist artist = getIntent().getParcelableExtra(ARTIST_EXTRA);
        return ArtistFragment.newInstance(artist);
    }

    @Override
    public boolean isToolbarCollapsing() {
        return true;
    }

}
