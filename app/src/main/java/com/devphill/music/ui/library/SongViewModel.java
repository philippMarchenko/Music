package com.devphill.music.ui.library;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;

import android.view.Gravity;
import android.view.View;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
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
import com.devphill.music.ui.common.playlist.AppendPlaylistDialogFragment;
import com.devphill.music.ui.library.album.AlbumActivity;
import com.devphill.music.ui.library.artist.ArtistActivity;
import com.devphill.music.ui.library.net_songs.Downloader;
import com.devphill.music.view.ViewUtils;
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

public class SongViewModel extends BaseObservable {

    private static final String TAG_PLAYLIST_DIALOG = "SongViewModel.PlaylistDialog";
    public static final String LOG_TAG = "SongViewModel";

    @Inject protected MusicStore mMusicStore;
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
    private Song mReference;

    private final ObservableField<Bitmap> mArtwork;

    public SongViewModel(BaseActivity activity, List<Song> songs) {
        this(activity, activity.getSupportFragmentManager(),
                activity.bindUntilEvent(ActivityEvent.DESTROY), songs);
    }

    public SongViewModel(BaseFragment fragment, List<Song> songs) {
        this(fragment.getActivity(), fragment.getFragmentManager(),
                fragment.bindUntilEvent(FragmentEvent.DESTROY), songs);
    }

    public SongViewModel(Activity activity, FragmentManager fragmentManager,
                         LifecycleTransformer<?> lifecycleTransformer, List<Song> songs) {
        mContext = activity;
        mActivity = activity;
        mFragmentManager = fragmentManager;
        mLifecycleTransformer = lifecycleTransformer;
        mSongList = songs;

        mArtwork = new ObservableField<>();



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
        mNowPlayingSubscription = isPlaying()
                .compose(bindToLifecycle())
                .subscribe(isPlaying -> {
                    mIsPlaying = isPlaying;
                    notifyPropertyChanged(BR.nowPlayingIndicatorVisibility);
                }, throwable -> {
                    Timber.e(throwable, "Failed to update playing indicator");
                });


        Glide.with(mContext)
                .load(mReference.getArtistImageUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        if(resource != null){
                        //     mArtwork.set(resource);
                            mArtwork.set(ViewUtils.drawableToBitmap(mContext.getResources().getDrawable(R.drawable.art_default)));

                        }
                        else{
                        }

                    }
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

    public ObservableField<Bitmap> getArtwork() {
        return mArtwork;
    }

    public View.OnClickListener onClickSong() {
        return v -> {
            mPlayerController.setQueue(mSongList, mIndex);
            mPlayerController.play();

         /*   if (mPrefStore.openNowPlayingOnNewQueue() && mActivity instanceof BaseLibraryActivity) {
                ((BaseLibraryActivity) mActivity).expandBottomSheet();
            }
*/


        };
    }

    public View.OnClickListener onClickMenu() {
        return v -> {
            final PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
            menu.inflate(R.menu.instance_song);
            menu.setOnMenuItemClickListener(onMenuItemClick());
            menu.show();
        };
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClick() {
        return menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_queue_item_next:
                    mPlayerController.queueNext(mReference);
                    return true;
                case R.id.menu_item_queue_item_last:
                    mPlayerController.queueLast(mReference);
                    return true;
                case R.id.menu_item_navigate_to_artist:
                    mMusicStore.findArtistById(mReference.getArtistId()).subscribe(
                            artist -> {
                                mContext.startActivity(ArtistActivity.newIntent(mContext, artist));
                            }, throwable -> {
                                Timber.e(throwable, "Failed to find artist");
                            });

                    return true;
                case R.id.menu_item_navigate_to_album:
                    mMusicStore.findAlbumById(mReference.getAlbumId()).subscribe(
                            album -> {
                                mContext.startActivity(AlbumActivity.newIntent(mContext, album));
                            }, throwable -> {
                                Timber.e(throwable, "Failed to find album", throwable);
                            });
                    return true;
                case R.id.menu_item_add_to_playlist:
                    new AppendPlaylistDialogFragment.Builder(mContext, mFragmentManager)
                            .setSongs(mReference)
                            .showSnackbarIn(R.id.list)
                            .show(TAG_PLAYLIST_DIALOG);
                    return true;
            }
            return false;
        };
    }

}
