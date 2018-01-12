package com.devphill.music.ui.nowplaying;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SimpleArrayMap;
import android.transition.Transition;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.devphill.music.BR;
import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.lastfm.api.LastFmApi;
import com.devphill.music.lastfm.api.LastFmService;
import com.devphill.music.lastfm.model.Image;
import com.devphill.music.lastfm.model.LfmArtist;
import com.devphill.music.model.Song;
import com.devphill.music.ui.BaseActivity;
import com.devphill.music.data.store.PreferenceStore;
import com.devphill.music.ui.BaseFragment;
import com.devphill.music.player.MusicPlayer;
import com.devphill.music.player.PlayerController;
import com.devphill.music.view.GestureView;
import com.devphill.music.view.ViewUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NowPlayingArtworkViewModel extends BaseObservable {

    private static final String LOG_TAG = "NowPlayingTag";

    @Inject PreferenceStore mPrefStore;
    @Inject PlayerController mPlayerController;

    private Context mContext;
    private Bitmap mArtwork;
    private boolean mPlaying;

    private Song mSong;

    private LastFmService lastFmService;
    private SimpleArrayMap<String, Observable<LfmArtist>> mCachedArtistInfo;


    public NowPlayingArtworkViewModel(BaseActivity activity) {
        this(activity, activity.bindToLifecycle());
    }

    public NowPlayingArtworkViewModel(BaseFragment fragment) {
        this(fragment.getContext(), fragment.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }

    @SuppressWarnings("unchecked")
    private NowPlayingArtworkViewModel(Context context, LifecycleTransformer<?> transformer) {
        mContext = context;
        JockeyApplication.getComponent(context).inject(this);

        Log.d(LOG_TAG, "NowPlayingArtworkViewModel ");

        lastFmService = LastFmApi.getService(context);
        mCachedArtistInfo = new SimpleArrayMap<>();

       // parseGoogle();

        mPlayerController.getNowPlaying()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        song -> {
                            getArtistInfo(song.getArtistName().replaceAll("\\([^)]+\\)", ""))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            lfmArtist -> {
                                                Image hero = lfmArtist.getImageBySize(Image.Size.MEGA);
                                                Log.d(LOG_TAG, "hero.getUrl() " + hero.getUrl());

                                                if(hero.getUrl().isEmpty()){
                                                    setArtwork(Bitmap.createBitmap(ViewUtils.drawableToBitmap(context.getResources().getDrawable(R.drawable.art_default))));
                                                }

                                                Glide.with(context)
                                                        .load(hero.getUrl())
                                                        .asBitmap()
                                                        .into(new SimpleTarget<Bitmap>() {
                                                            @Override
                                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                                setArtwork(resource);
                                                                Log.d(LOG_TAG, "onResourceReady ");
                                                            }
                                                        });
                                            },
                                            throwable -> {
                                                Timber.e(throwable, "Failed to get Last.fm artist info");
                                            });



                        },
                        throwable -> {
                            Log.d(LOG_TAG, "Ошибка " + throwable.getMessage());
                        });




       /* mPlayerController.getArtwork()
                .compose((LifecycleTransformer<Bitmap>) transformer)
                .subscribe(this::setArtwork,
                        throwable -> Timber.e(throwable, "Failed to set artwork"));*/

        mPlayerController.isPlaying()
                .compose((LifecycleTransformer<Boolean>) transformer)
                .subscribe(this::setPlaying,
                        throwable -> Timber.e(throwable, "Failed to update playing state"));
    }

    private void parseGoogle(){

        // can only grab first 100 results
        String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
        String url = "https://www.google.com/search?site=imghp&tbm=isch&source=hp&q=oxxi=oxxymiron&gws_rd=cr";

        List<String> resultUrls = new ArrayList<String>();

        try {
            Document doc = Jsoup.connect(url).userAgent(userAgent).referrer("https://www.google.com/").get();

            Elements elements = doc.select("div.rg_meta");

            JSONObject jsonObject;
            JsonParser jsonParser = new JsonParser();
            for (Element element : elements) {
                if (element.childNodeSize() > 0) {
                  //  jsonObject = (JSONObject) new JSONParser().parse(element.childNode(0).toString());
                   // resultUrls.add((String) jsonObject.get("ou"));
                    JsonElement jsonElement = jsonParser.parse(element.childNode(0).toString());
                    resultUrls.add(jsonElement.toString());
                }
            }
            Log.d(LOG_TAG, "number of results: " + resultUrls.size());

            for (String imageUrl : resultUrls) {
                System.out.println(imageUrl);
            }

        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception of parse: " +  e.getMessage());
        }
    }

    private void setPlaying(boolean playing) {
        mPlaying = playing;
        notifyPropertyChanged(BR.tapIndicator);
    }

    public int getPortraitArtworkHeight() {
        // Only used when in portrait orientation
        int reservedHeight = (int) mContext.getResources().getDimension(R.dimen.player_frame_peek);

        // Default to a square view, so set the height equal to the width
        //noinspection SuspiciousNameCombination
        int preferredHeight = mContext.getResources().getDisplayMetrics().widthPixels;
        int maxHeight = mContext.getResources().getDisplayMetrics().heightPixels - reservedHeight;

        return Math.min(preferredHeight, maxHeight);
    }

    private void setArtwork(Bitmap artwork) {
        mArtwork = artwork;
        notifyPropertyChanged(BR.nowPlayingArtwork);
    }

    @Bindable
    public Drawable getNowPlayingArtwork() {
        if (mArtwork == null) {
            return ContextCompat.getDrawable(mContext, R.drawable.art_default_xl);
        } else {
            return new BitmapDrawable(mContext.getResources(), mArtwork);
        }
    }

    public Observable<LfmArtist> getArtistInfo(String artistName) {
        Observable<LfmArtist> result = mCachedArtistInfo.get(artistName);
        if (result == null) {
            result = lastFmService.getArtistInfo(artistName)
                    .map(response -> {
                        if (!response.isSuccessful()) {
                            String message = "Call to getArtistInfo failed with response code "
                                    + response.code()
                                    + "\n" + response.message();

                            throw Exceptions.propagate(new IOException(message));
                        }

                        return response.body().getArtist();
                    })
                    .cache();

            mCachedArtistInfo.put(artistName, result);
        }
        return result;
    }


    @Bindable
    public boolean getGesturesEnabled() {
        return mPrefStore.enableNowPlayingGestures();
    }

    @Bindable
    public Drawable getTapIndicator() {
        return ContextCompat.getDrawable(mContext,
                mPlaying
                        ? R.drawable.ic_pause_36dp
                        : R.drawable.ic_play_arrow_36dp);
    }

    public GestureView.OnGestureListener getGestureListener() {
        return new GestureView.OnGestureListener() {
            @Override
            public void onLeftSwipe() {
                mPlayerController.skip();
            }

            @Override
            public void onRightSwipe() {
                mPlayerController.getQueuePosition()
                        .take(1)
                        .flatMap((queuePosition) -> {
                            // Wrap to end of the queue when repeat all is enabled
                            if (queuePosition == 0
                                    && mPrefStore.getRepeatMode() == MusicPlayer.REPEAT_ALL) {
                                return mPlayerController.getQueue()
                                        .take(1)
                                        .map(List::size)
                                        .map(size -> size - 1);
                            } else {
                                return Observable.just(queuePosition - 1);
                            }
                        })
                        .subscribe((queuePosition) -> {
                            if (queuePosition >= 0) {
                                mPlayerController.changeSong(queuePosition);
                            } else {
                                mPlayerController.seek(0);
                            }
                        }, throwable -> {
                            Timber.e(throwable, "Failed to handle skip gesture");
                        });
            }

            @Override
            public void onTap() {
                mPlayerController.togglePlay();
                notifyPropertyChanged(BR.tapIndicator);
            }
        };
    }

}
