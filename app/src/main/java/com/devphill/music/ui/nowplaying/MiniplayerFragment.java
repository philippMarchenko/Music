package com.devphill.music.ui.nowplaying;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.devphill.music.JockeyApplication;
import com.devphill.music.data.store.ThemeStore;
import com.devphill.music.databinding.FragmentMiniplayerBinding;
import com.devphill.music.ui.BaseFragment;

import javax.inject.Inject;

public class MiniplayerFragment extends BaseFragment {

    private FragmentMiniplayerBinding mBinding;

    @Inject ThemeStore mThemeStore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JockeyApplication.getComponent(getContext()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentMiniplayerBinding.inflate(inflater, container, false);
        mBinding.setViewModel(new MiniplayerViewModel(this));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ProgressBar progressBar = mBinding.miniplayerProgress;
            LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();

            Drawable progress = progressBarDrawable.findDrawableByLayerId(android.R.id.progress);
            progress.setColorFilter(mThemeStore.getAccentColor(), PorterDuff.Mode.SRC_ATOP);
        }

        return mBinding.getRoot();
    }

}
