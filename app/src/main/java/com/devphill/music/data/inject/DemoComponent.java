package com.devphill.music.data.inject;

import com.devphill.music.lastfm.data.inject.DemoLastFmModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, PlayerModule.class, DemoModule.class,
        DemoLastFmModule.class})
public interface DemoComponent extends JockeyGraph {
}
