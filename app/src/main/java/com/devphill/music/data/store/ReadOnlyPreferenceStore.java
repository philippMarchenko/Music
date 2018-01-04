package com.devphill.music.data.store;

import android.media.audiofx.Equalizer;

import com.devphill.music.data.annotations.AccentTheme;
import com.devphill.music.data.annotations.BaseTheme;
import com.devphill.music.data.annotations.PrimaryTheme;
import com.devphill.music.data.annotations.StartPage;

import java.util.Set;

public interface ReadOnlyPreferenceStore {

    boolean showFirstStart();
    boolean allowLogging();
    boolean useMobileNetwork();

    boolean openNowPlayingOnNewQueue();
    boolean enableNowPlayingGestures();
    @StartPage int getDefaultPage();
    @PrimaryTheme int getPrimaryColor();
    @AccentTheme
    int getAccentColor();
    @BaseTheme int getBaseColor();
    @PrimaryTheme int getIconColor();

    boolean resumeOnHeadphonesConnect();
    boolean isShuffled();
    int getRepeatMode();

    long getLastSleepTimerDuration();

    int getEqualizerPresetId();
    boolean getEqualizerEnabled();
    Equalizer.Settings getEqualizerSettings();

    Set<String> getIncludedDirectories();
    Set<String> getExcludedDirectories();

}
