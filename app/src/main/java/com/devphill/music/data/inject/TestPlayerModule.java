package com.devphill.music.data.inject;

import com.devphill.music.player.MockPlayerController;
import com.devphill.music.player.PlayerController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestPlayerModule {

    @Provides
    @Singleton
    public PlayerController providePlayerController() {
        return new MockPlayerController();
    }

}
