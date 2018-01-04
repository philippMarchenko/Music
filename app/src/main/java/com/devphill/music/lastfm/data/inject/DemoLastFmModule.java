package com.devphill.music.lastfm.data.inject;

import android.content.Context;

import com.devphill.music.lastfm.data.store.DemoLastFmStore;
import com.devphill.music.lastfm.data.store.LastFmStore;
import com.devphill.music.lastfm.data.store.NetworkLastFmStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DemoLastFmModule {

    @Provides
    @Singleton
    public LastFmStore provideLastFmStore(Context context) {
        return new DemoLastFmStore(context);
    }

}
