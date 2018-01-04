package com.devphill.music.ui.library;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.devphill.music.R;
import com.devphill.music.ui.library.folders.FoldersFragment;
import com.devphill.music.ui.library.my_downloads.MyDownloadsFragment;
import com.devphill.music.ui.library.net_songs.NetSongsFragment;
import com.devphill.music.ui.nowplaying.GenreFragment;

class LibraryPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private PlaylistFragment playlistFragment;
    private SongFragment songFragment;
    private ArtistFragment artistFragment;
    private AlbumListFragment albumFragment;
    private GenreFragment genreFragment;
    private NetSongsFragment netSongsFragment;
    private FoldersFragment foldersFragment;
    private MyDownloadsFragment myDownloadsFragment;


    public LibraryPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        Log.d("LibraryPagerAdapter", "LibraryPagerAdapter " );

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (netSongsFragment == null) {
                    netSongsFragment = new NetSongsFragment();
                }
                return netSongsFragment;
            case 1:
                if (songFragment == null) {
                    songFragment = new SongFragment();
                }
                return songFragment;
            case 2:
            if (myDownloadsFragment == null) {
                myDownloadsFragment = new MyDownloadsFragment();
            }
            return myDownloadsFragment;
            case 3:
                if (foldersFragment == null) {
                    foldersFragment = new FoldersFragment();
                }
                return foldersFragment;
            case 4:
                if (playlistFragment == null) {
                    playlistFragment = new PlaylistFragment();
                }
                return playlistFragment;
            case 5:
                if (artistFragment == null) {
                    artistFragment = new ArtistFragment();
                }
                return artistFragment;
            case 6:
                if (albumFragment == null) {
                    albumFragment = new AlbumListFragment();
                }
                return albumFragment;
            case 7:
                if (genreFragment == null) {
                    genreFragment = new GenreFragment();
                }
                return genreFragment;
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                 return mContext.getResources().getString(R.string.header_songs_net);
            case 1:
                return mContext.getResources().getString(R.string.header_songs);
            case 2:
                return mContext.getResources().getString(R.string.header_my_downloads);
            case 3:
                return mContext.getResources().getString(R.string.header_folders);
            case 4:
                return mContext.getResources().getString(R.string.header_playlists);
            case 5:
                return mContext.getResources().getString(R.string.header_artists);
            case 6:
                return mContext.getResources().getString(R.string.header_albums);
            case 7:
                return mContext.getResources().getString(R.string.header_genres);
            default:
                return "Page " + position;
        }
    }

}
