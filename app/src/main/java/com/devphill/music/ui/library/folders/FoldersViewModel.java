package com.devphill.music.ui.library.folders;


import com.devphill.music.data.store.PreferenceStore;
import com.devphill.music.data.store.ThemeStore;
import com.devphill.music.lastfm.data.store.LastFmStore;
import com.devphill.music.model.Song;
import com.devphill.music.ui.BaseFragment;
import com.marverenic.adapter.HeterogeneousAdapter;

import java.util.List;



public class FoldersViewModel {

    private BaseFragment mFragment;

    private LastFmStore mLfmStore;
    private PreferenceStore mPrefStore;
    private ThemeStore mThemeStore;

    private HeterogeneousAdapter mAdapter;

    private Song mReference;
   // private List<Song> mSongList;


    public FoldersViewModel(BaseFragment fragment, List<String> folders) {

        mFragment = fragment;


      //  mSongList = folders;

    }
}
