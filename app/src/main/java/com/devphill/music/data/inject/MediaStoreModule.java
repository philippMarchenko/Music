package com.devphill.music.data.inject;

import android.content.Context;

import com.devphill.music.data.store.LocalMusicStore;
import com.devphill.music.data.store.LocalPlayCountStore;
import com.devphill.music.data.store.LocalPlaylistStore;
import com.devphill.music.data.store.MusicStore;
import com.devphill.music.data.store.PlayCountStore;
import com.devphill.music.data.store.PlaylistStore;
import com.devphill.music.data.store.PreferenceStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MediaStoreModule {

    @Provides
    @Singleton
    public MusicStore provideMusicStore(Context context, PreferenceStore preferenceStore) {
        return new LocalMusicStore(context, preferenceStore);
    }

    @Provides
    @Singleton
    public PlaylistStore providePlaylistStore(Context context, MusicStore musicStore,
                                              PlayCountStore playCountStore) {
        return new LocalPlaylistStore(context, musicStore, playCountStore);
    }

    @Provides
    @Singleton
    public PlayCountStore providePlayCountStore(Context context) {
        return new LocalPlayCountStore(context);
    }
}
