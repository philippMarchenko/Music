package com.devphill.music.data.inject;

import android.content.Context;

import com.devphill.music.data.store.DemoMusicStore;
import com.devphill.music.data.store.DemoPlaylistStore;
import com.devphill.music.data.store.LocalPlayCountStore;
import com.devphill.music.data.store.MusicStore;
import com.devphill.music.data.store.PlayCountStore;
import com.devphill.music.data.store.PlaylistStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DemoModule {

    @Provides
    @Singleton
    public MusicStore provideMusicStore(Context context) {
        return new DemoMusicStore(context);
    }

    @Provides
    @Singleton
    public PlaylistStore providePlaylistStore(Context context) {
        return new DemoPlaylistStore(context);
    }

    @Provides
    @Singleton
    public PlayCountStore providePlayCountStore(Context context) {
        return new LocalPlayCountStore(context);
    }

}
