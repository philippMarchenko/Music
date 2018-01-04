package com.devphill.music.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

import com.devphill.music.R;
import com.devphill.music.databinding.ActivityLibraryBaseWrapperBinding;

public abstract class BaseLibraryActivity extends SingleFragmentActivity {

    private static final String KEY_WAS_NOW_PLAYING_EXPANDED = "NowPlayingPageExpanded";

    private ActivityLibraryBaseWrapperBinding mBinding;
    private BaseLibraryActivityViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreateLayout(@Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_library_base_wrapper);
        mViewModel = new BaseLibraryActivityViewModel(this, !isToolbarCollapsing());
        mBinding.setViewModel(mViewModel);

        if (savedInstanceState != null) {
            boolean expanded = savedInstanceState.getBoolean(KEY_WAS_NOW_PLAYING_EXPANDED, false);
            if (expanded) expandBottomSheet();
        }
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.library_base_wrapper_container;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        BottomSheetBehavior<View> bottomSheet = BottomSheetBehavior.from(mBinding.miniplayerHolder);
        boolean expanded = bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED;
        outState.putBoolean(KEY_WAS_NOW_PLAYING_EXPANDED, expanded);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.onActivityExitForeground();
        mBinding.executePendingBindings();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.onActivityEnterForeground();
        mBinding.executePendingBindings();
    }

    @Override
    public void onBackPressed() {
        BottomSheetBehavior<View> bottomSheet = BottomSheetBehavior.from(mBinding.miniplayerHolder);
        if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void showSnackbar(String message) {
        if (mBinding.libraryBaseWrapperContainer.getVisibility() == View.VISIBLE) {
            super.showSnackbar(message);
        }
    }

    public boolean isToolbarCollapsing() {
        return false;
    }

    public void expandBottomSheet() {
        BottomSheetBehavior<View> bottomSheet = BottomSheetBehavior.from(mBinding.miniplayerHolder);
        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}
