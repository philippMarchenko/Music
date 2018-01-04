package com.devphill.music.ui.nowplaying;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.SeekBar;

import com.devphill.music.BR;
import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.model.Song;
import com.devphill.music.player.PlayerController;
import com.devphill.music.view.ViewUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class MiniplayerViewModel extends BaseObservable {

    private Context mContext;

    @Inject PlayerController mPlayerController;

    @Nullable
    private Song mSong;
    private boolean mPlaying;

    private final ObservableField<Bitmap> mArtwork;
    private final ObservableInt mDuration;
    private final ObservableInt mProgress;

    private final ObservableInt mSeekbarPosition;
    private final ObservableInt mCurrentPositionObservable;

    private boolean mUserTouchingProgressBar;

    public MiniplayerViewModel(BaseFragment fragment) {
        mContext = fragment.getContext();
        JockeyApplication.getComponent(mContext).inject(this);

        mArtwork = new ObservableField<>();
        mProgress = new ObservableInt();
        mDuration = new ObservableInt();

        mCurrentPositionObservable = new ObservableInt();
        mSeekbarPosition = new ObservableInt();

        setSong(null);

        mPlayerController.getNowPlaying()
                .compose(fragment.bindToLifecycle())
                .subscribe(this::setSong, throwable -> {
                    Timber.e(throwable, "Failed to set song");
                });

        mPlayerController.isPlaying()
                .compose(fragment.bindToLifecycle())
                .subscribe(this::setPlaying, throwable -> {
                    Timber.e(throwable, "Failed to set playing state");
                });

        mPlayerController.getCurrentPosition()
                .compose(fragment.bindToLifecycle())
                .subscribe(mProgress::set, throwable -> {
                    Timber.e(throwable, "Failed to set progress");
                });

        mPlayerController.getDuration()
                .compose(fragment.bindToLifecycle())
                .subscribe(mDuration::set, throwable -> {
                    Timber.e(throwable, "Failed to set duration");
                });

        mPlayerController.getArtwork()
                .compose(fragment.bindToLifecycle())
                .map(artwork -> {
                    if (artwork == null) {
                        return ViewUtils.drawableToBitmap(
                                ContextCompat.getDrawable(mContext, R.drawable.art_default));
                    } else {
                        return artwork;
                    }
                })
                .subscribe(mArtwork::set, throwable -> {
                    Timber.e(throwable, "Failed to set artwork");
                });

        mPlayerController.getCurrentPosition()
                .compose(fragment.bindToLifecycle())
                .subscribe(
                        position -> {
                            mCurrentPositionObservable.set(position);
                            if (!mUserTouchingProgressBar) {
                                mSeekbarPosition.set(position);
                            }
                        },
                        throwable -> {
                            Timber.e(throwable, "failed to update position");
                        });
    }

    private void setSong(@Nullable Song song) {
        mSong = song;
        notifyPropertyChanged(BR.songTitle);
        notifyPropertyChanged(BR.songArtist);
    }

    private void setPlaying(boolean playing) {
        mPlaying = playing;
        notifyPropertyChanged(BR.togglePlayIcon);
    }

    @Bindable
    public String getSongTitle() {
        if (mSong == null) {
            return mContext.getResources().getString(R.string.nothing_playing);
        } else {
            return mSong.getSongName();
        }
    }

    @Bindable
    public String getSongArtist() {
        if (mSong == null) {
            return null;
        } else {
            return mSong.getArtistName();
        }
    }

    @Bindable
    public ObservableInt getSongDuration() {
        return mDuration;
    }

    public ObservableField<Bitmap> getArtwork() {
        return mArtwork;
    }

    public ObservableInt getProgress() {
        return mProgress;
    }

    @Bindable
    public Drawable getTogglePlayIcon() {
        if (mPlaying) {
            return ContextCompat.getDrawable(mContext, R.drawable.ic_pause_32dp);
        } else {
            return ContextCompat.getDrawable(mContext, R.drawable.ic_play_arrow_32dp);
        }
    }

    public View.OnClickListener onClickTogglePlay() {
        return v -> mPlayerController.togglePlay();
    }

    public View.OnClickListener onClickSkip() {
        return v -> mPlayerController.skip();
    }

    @Bindable
    public boolean getSeekbarEnabled() {
        return mSong != null;
    }

    @Bindable
    public ObservableInt getSeekBarPosition() {
        return mSeekbarPosition;
    }

    public SeekBar.OnSeekBarChangeListener onSeek() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekbarPosition.set(progress);
                if (fromUser) {
                    notifyPropertyChanged(BR.seekBarHeadMarginLeft);

                    if (!mUserTouchingProgressBar) {
                        // For keyboards and non-touch based things
                        onStartTrackingTouch(seekBar);
                        onStopTrackingTouch(seekBar);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUserTouchingProgressBar = true;
               // animateSeekBarHeadIn();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mUserTouchingProgressBar = false;
             //   animateSeekBarHeadOut();

                mPlayerController.seek(seekBar.getProgress());
                mCurrentPositionObservable.set(seekBar.getProgress());
            }
        };
    }

}
