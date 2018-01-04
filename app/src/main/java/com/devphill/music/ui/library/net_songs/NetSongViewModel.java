package com.devphill.music.ui.library.net_songs;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.devphill.music.BR;
import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.data.store.MusicStore;
import com.devphill.music.data.store.PreferenceStore;
import com.devphill.music.model.Song;
import com.devphill.music.player.PlayerController;
import com.devphill.music.ui.BaseActivity;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.ui.BaseLibraryActivity;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class NetSongViewModel extends BaseObservable {

    private static final String TAG_PLAYLIST_DIALOG = "SongViewModel.PlaylistDialog";
    public static final String LOG_TAG = "NetSongViewModel";

    @Inject
    protected MusicStore mMusicStore;
    @Inject protected PreferenceStore mPrefStore;
    @Inject protected PlayerController mPlayerController;

    private Context mContext;
    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private LifecycleTransformer<?> mLifecycleTransformer;
    private Subscription mNowPlayingSubscription;

    private List<Song> mSongList;
    private int mIndex;
    private boolean mIsPlaying;
    private boolean mIsDownloading;
    private Song mReference;
    Downloader downloader;

    private final ObservableInt mProgress;
    private final ObservableField<Bitmap> download_image;

    public NetSongViewModel(BaseActivity activity, List<Song> songs) {
        this(activity, activity.getSupportFragmentManager(),
                activity.bindUntilEvent(ActivityEvent.DESTROY), songs);
    }

    public NetSongViewModel(BaseFragment fragment, List<Song> songs) {
        this(fragment.getActivity(), fragment.getFragmentManager(),
                fragment.bindUntilEvent(FragmentEvent.DESTROY), songs);
    }

    public NetSongViewModel(Activity activity, FragmentManager fragmentManager,
                         LifecycleTransformer<?> lifecycleTransformer, List<Song> songs) {
        mContext = activity;
        mActivity = activity;
        mFragmentManager = fragmentManager;
        mLifecycleTransformer = lifecycleTransformer;
        mSongList = songs;

        mProgress = new ObservableInt();
        download_image = new  ObservableField<Bitmap>();
        download_image.set(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.download));

        downloader = new Downloader(mContext);

        downloader.createMyFolder();


        JockeyApplication.getComponent(mContext).inject(this);
    }

    public void setIndex(int index) {
        setSong(mSongList, index);
    }

    protected int getIndex() {
        return mIndex;
    }

    protected Song getReference() {
        return mReference;
    }

    protected List<Song> getSongs() {
        return mSongList;
    }

    private <T> LifecycleTransformer<T> bindToLifecycle() {
        //noinspection unchecked
        return (LifecycleTransformer<T>) mLifecycleTransformer;
    }

    protected Observable<Boolean> isPlaying() {
        return mPlayerController.getNowPlaying()
                .map(playing -> playing != null && playing.equals(getReference()));
    }

    public void setSong(List<Song> songList, int index) {
        mSongList = songList;
        mIndex = index;
        mReference = songList.get(index);

        if (mNowPlayingSubscription != null) {
            mNowPlayingSubscription.unsubscribe();
        }

        mIsPlaying = false;
        mIsDownloading = false;

        mNowPlayingSubscription = isPlaying()
                .compose(bindToLifecycle())
                .subscribe(isPlaying -> {
                    mIsPlaying = isPlaying;
                    notifyPropertyChanged(BR.nowPlayingIndicatorVisibility);
                }, throwable -> {
                    Timber.e(throwable, "Failed to update playing indicator");
                });

        notifyPropertyChanged(BR.title);
        notifyPropertyChanged(BR.detail);
    }

    @Bindable
    public int getNowPlayingIndicatorVisibility() {
        if (mIsPlaying) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    @Bindable
    public String getTitle() {
        return mReference.getSongName();
    }

    @Bindable
    public String getDetail() {
        return mContext.getString(R.string.format_compact_song_info,
                mReference.getArtistName(), mReference.getAlbumName());
    }

    public View.OnClickListener onClickSong() {
        return v -> {
            mPlayerController.setQueue(mSongList, mIndex);
            mPlayerController.play();

            if (mPrefStore.openNowPlayingOnNewQueue() && mActivity instanceof BaseLibraryActivity) {
                ((BaseLibraryActivity) mActivity).expandBottomSheet();
            }

        };
    }

    public View.OnClickListener onClickDownload() {
        return v -> {
            mIsDownloading = true;

            Log.d(LOG_TAG,"onClickDownload ");
            Log.d(LOG_TAG,"getSongName " +  mSongList.get(mIndex).getSongName());


            downloader.downloadFile("https://" +
                            mSongList.get(mIndex).getLocation().getHost() +
                            mSongList.get(mIndex).getLocation().getPath(),
                    mSongList.get(mIndex).getArtistName() + " - " + mSongList.get(mIndex).getSongName()).
                    subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Integer>() {
                        @Override
                        public void onNext(Integer value) {

                          //  Log.d(LOG_TAG,"onNext  persent " + value);
                            if(mIsDownloading){
                                mProgress.set(value);
                            }

                          //  notifyChange();

                        }

                        @Override
                        public void onError(Throwable e) {
                            //Handle error
                        }

                        @Override
                        public void onComplete() {
                            Log.d(LOG_TAG,"Load file finish! ");

                        }
                    });
        };
    }

    @Bindable
    public ObservableInt getProgress() {

            if(mIsDownloading){

                Log.d(LOG_TAG,"getProgress " + mIsDownloading);
                Log.d(LOG_TAG,"getSongName " +  mSongList.get(mIndex).getSongName());
                return mProgress;
            }
            else {
                Log.d(LOG_TAG,"getProgress " + mIsDownloading);
                Log.d(LOG_TAG,"getSongName " +  mSongList.get(mIndex).getSongName());
                mProgress.set(0);
                return mProgress;
            }
}

    @Bindable
    public ObservableField<Bitmap> getDownload_image() {

        if(mIsDownloading){
            download_image.set(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cancel));
            return download_image;
        }
        else{
            download_image.set(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.download));
            return download_image;
        }
    }
}
