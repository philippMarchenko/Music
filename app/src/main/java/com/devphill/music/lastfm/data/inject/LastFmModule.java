package com.devphill.music.lastfm.data.inject;

import android.content.Context;

import com.devphill.music.lastfm.api.LastFmApi;
import com.devphill.music.lastfm.api.LastFmService;
import com.devphill.music.lastfm.data.store.LastFmStore;
import com.devphill.music.lastfm.data.store.NetworkLastFmStore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LastFmModule {

    @Provides
    @Singleton
    public LastFmService provideLastFmService(Context context) {
        return LastFmApi.getService(context);
    }

    @Provides
    @Singleton
    public LastFmStore provideLastFmStore(LastFmService service) {
        return new NetworkLastFmStore(service);
    }

}
