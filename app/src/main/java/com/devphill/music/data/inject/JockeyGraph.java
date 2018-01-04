package com.devphill.music.data.inject;

import com.devphill.music.player.MusicPlayer;
import com.devphill.music.player.ServicePlayerController;
import com.devphill.music.ui.BaseActivity;
import com.devphill.music.ui.BaseLibraryActivityViewModel;
import com.devphill.music.ui.about.AboutActivity;
import com.devphill.music.ui.common.LibraryEmptyState;
import com.devphill.music.ui.common.ShuffleAllSection;
import com.devphill.music.ui.common.playlist.AppendPlaylistDialogFragment;
import com.devphill.music.ui.common.playlist.CreatePlaylistDialogFragment;
import com.devphill.music.ui.common.playlist.PlaylistCollisionDialogFragment;
import com.devphill.music.ui.library.AlbumListFragment;
import com.devphill.music.ui.library.AlbumViewModel;
import com.devphill.music.ui.library.ArtistFragment;
import com.devphill.music.ui.library.ArtistViewModel;
import com.devphill.music.ui.library.GenreViewModel;
import com.devphill.music.ui.library.LibraryActivity;
import com.devphill.music.ui.library.LibraryFragment;
import com.devphill.music.ui.library.PlaylistFragment;
import com.devphill.music.ui.library.PlaylistViewModel;
import com.devphill.music.ui.library.SongFragment;
import com.devphill.music.ui.library.SongViewModel;
import com.devphill.music.ui.library.album.AlbumFragment;
import com.devphill.music.ui.library.folders.FoldersFragment;
import com.devphill.music.ui.library.net_songs.NetSongViewModel;
import com.devphill.music.ui.library.net_songs.NetSongsFragment;
import com.devphill.music.ui.library.playlist.AutoPlaylistActivity;
import com.devphill.music.ui.library.playlist.edit.AutoPlaylistEditFragment;
import com.devphill.music.ui.library.playlist.edit.RuleHeaderViewModel;
import com.devphill.music.ui.library.playlist.edit.RuleViewModel;
import com.devphill.music.ui.nowplaying.GenreFragment;
import com.devphill.music.ui.nowplaying.MiniplayerFragment;
import com.devphill.music.ui.nowplaying.MiniplayerViewModel;
import com.devphill.music.ui.nowplaying.NowPlayingArtworkViewModel;
import com.devphill.music.ui.nowplaying.NowPlayingControllerViewModel;
import com.devphill.music.ui.nowplaying.NowPlayingFragment;
import com.devphill.music.ui.nowplaying.QueueFragment;
import com.devphill.music.ui.search.SearchActivity;
import com.devphill.music.ui.search.SearchFragment;
import com.devphill.music.ui.settings.DirectoryListFragment;
import com.devphill.music.ui.settings.EqualizerFragment;
import com.devphill.music.ui.settings.PreferenceFragment;
import com.devphill.music.widget.BaseWidget;

public interface JockeyGraph {

    void injectBaseActivity(BaseActivity baseActivity);

    void inject(LibraryActivity activity);
    void inject(SearchActivity activity);
    void inject(AboutActivity activity);

    void inject(BaseWidget widget);

    void inject(LibraryFragment fragment);
    void inject(SearchFragment fragment);
    void inject(SongFragment fragment);
    void inject(AlbumListFragment fragment);
    void inject(AlbumFragment fragment);
    void inject(FoldersFragment fragment);
    void inject(NetSongsFragment fragment);
    void inject(ArtistFragment fragment);
    void inject(PlaylistFragment fragment);
    void inject(GenreFragment fragment);
    void inject(NowPlayingFragment fragment);
    void inject(QueueFragment fragment);
    void inject(EqualizerFragment fragment);
    void inject(PreferenceFragment fragment);
    void inject(DirectoryListFragment fragment);
    void inject(com.devphill.music.ui.library.artist.ArtistFragment fragment);
    void inject(com.devphill.music.ui.library.playlist.PlaylistFragment playlistFragment);
    void inject(com.devphill.music.ui.library.genre.GenreFragment fragment);
    void inject(AutoPlaylistEditFragment fragment);

    void inject(MiniplayerFragment fragment);

    void inject(CreatePlaylistDialogFragment dialogFragment);
    void inject(AppendPlaylistDialogFragment dialogFragment);
    void inject(PlaylistCollisionDialogFragment dialogFragment);

    void inject(BaseLibraryActivityViewModel viewModel);
    void inject(MiniplayerViewModel viewModel);
    void inject(NowPlayingControllerViewModel viewModel);
    void inject(NowPlayingArtworkViewModel viewModel);
    void inject(SongViewModel viewModel);
    void inject(NetSongViewModel viewModel);
    void inject(AlbumViewModel viewModel);
    void inject(ArtistViewModel viewModel);
    void inject(GenreViewModel viewModel);
    void inject(PlaylistViewModel viewModel);
    void inject(RuleHeaderViewModel viewModel);
    void inject(RuleViewModel viewModel);

    void inject(ShuffleAllSection section);

    void inject(LibraryEmptyState emptyState);

    void inject(ServicePlayerController.Listener broadcastReceiver);
    void inject(MusicPlayer musicPlayer);

}
