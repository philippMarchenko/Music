package com.devphill.music.data.inject;

import android.content.Context;

import com.devphill.music.data.store.PreferenceStore;
import com.devphill.music.player.PlayerController;
import com.devphill.music.player.ServicePlayerController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlayerModule {

    @Provides
    @Singleton
    public PlayerController providePlayerController(Context context, PreferenceStore prefs) {
        return new ServicePlayerController(context, prefs);
    }

}
