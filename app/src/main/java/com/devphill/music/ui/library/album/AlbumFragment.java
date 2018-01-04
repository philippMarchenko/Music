package com.devphill.music.ui.library.album;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devphill.music.JockeyApplication;
import com.devphill.music.data.store.MusicStore;
import com.devphill.music.databinding.FragmentAlbumBinding;
import com.devphill.music.model.Album;
import com.devphill.music.ui.BaseFragment;

import javax.inject.Inject;

public class AlbumFragment extends BaseFragment {

    private static final String ARG_ALBUM = "AlbumListFragment.AlBUM";

    @Inject MusicStore mMusicStore;

    private Album mAlbum;

    public static AlbumFragment newInstance(Album album) {
        AlbumFragment fragment = new AlbumFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_ALBUM, album);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JockeyApplication.getComponent(this).inject(this);

        mAlbum = getArguments().getParcelable(ARG_ALBUM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentAlbumBinding binding = FragmentAlbumBinding.inflate(inflater, container, false);
        binding.setViewModel(new AlbumViewModel(this, mAlbum, mMusicStore));

        setupToolbar(binding.toolbar);
        return binding.getRoot();
    }

    private void setupToolbar(Toolbar toolbar) {
        toolbar.setTitle(mAlbum.getAlbumName());
        setActivitySupportActionBar(toolbar);

        ActionBar actionBar = getActivitySupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}
