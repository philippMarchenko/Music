package com.devphill.music;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.bumptech.glide.Glide;
import com.devphill.music.data.inject.JockeyComponentFactory;
import com.devphill.music.data.inject.JockeyGraph;
import com.devphill.music.utils.CrashlyticsTree;
import com.devphill.music.utils.compat.JockeyPreferencesCompat;

import timber.log.Timber;

public class JockeyApplication extends Application {

    private JockeyGraph mComponent;

    @Override
    public void onCreate() {
        setupStrictMode();
        super.onCreate();

        setupCrashlytics();
        setupTimber();

        mComponent = createDaggerComponent();
        JockeyPreferencesCompat.upgradeSharedPreferences(this);
    }

    @NonNull
    protected JockeyGraph createDaggerComponent() {
        return JockeyComponentFactory.create(this);
    }

    private void setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
    }

    private void setupCrashlytics() {
     //   Fabric.with(this, new Crashlytics());
    }

    private void setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsTree());
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.with(this).onTrimMemory(level);
    }

    public static JockeyGraph getComponent(Fragment fragment) {
        return getComponent(fragment.getContext());
    }

    public static JockeyGraph getComponent(Context context) {
        return ((JockeyApplication) context.getApplicationContext()).mComponent;
    }
}
