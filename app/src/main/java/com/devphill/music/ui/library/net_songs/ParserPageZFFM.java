package com.devphill.music.ui.library.net_songs;

import android.net.Uri;
import android.util.Log;

import com.devphill.music.model.Constants;
import com.devphill.music.model.ObjectTrack;
import com.devphill.music.model.Song;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by Lerich on 22.11.2017.
 */

/**
 * Класс который получает обьект
 */
public class ParserPageZFFM extends Constants {

    private Document documentPage;
    private Elements elements;
    public String keywords;
    public String url;
    public String userAgent = null;
    public List<Song> objectTrackList = new ArrayList<>();
  //  public objectTrackList = new ArrayList<>();

    public int numberOfItems = 0;


    public ParserPageZFFM() {

    }

    public int getNumberOfItems(){
        return numberOfItems;
    }

    public Observable getTrackList(final String url, final String keywords, final String nextPage){
        return getTrackList(url, keywords + Constants.ZF_FM_NEXT_PAGE + nextPage);
    }

    /**
     *
     * Наблюдатель "Метод" возвращающий массив (список) треков
     * @param url
     * @param keywords
     * @return
     */
    public Observable getTrackList(final String url, final String keywords) {

        final Observable<List<Song>> observable =
                Observable.create(new ObservableOnSubscribe<List<Song>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<Song>> e) throws Exception {
                        //Use onNext to emit each item in the stream//

                        Log.d("ParserPageZFFM", "1 " + url + keywords);
                        DownloadPage downloadpage = new DownloadPage();
                        documentPage = downloadpage.getPage(url, keywords);
                        Log.d("ParserPageZFFM", "2" + documentPage);
                        Log.e("RxJavaX", "onSubscribe: 12");
                        objectTrackList = trackList(documentPage);
                        Log.e("RxJavaX", "onSubscribe: 123456789" + objectTrackList);
                        e.onNext(objectTrackList);
                        //Once the Observable has emitted all items in the sequence, call onComplete//
                        e.onComplete();
                    }
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    /*
        Метод парсинга списка треков
     */
    private List trackList(Document documentPage) {
        try {
            Elements searchContainer = documentPage.select(TRACK_GENERIC_CONTAINER);

            Elements elementsNumberOfItems = documentPage.select("b.grey");
            numberOfItems = Integer.parseInt(elementsNumberOfItems.text());
            Log.e("RxJavaX", "numberOfItems = " + numberOfItems);

            Elements itemTrackElements = searchContainer.select(TRACK_ITEM_CONTAINER);
            int numItemTrackElements = searchContainer.select(TRACK_ITEM_CONTAINER).size();
            Log.d("ParserPageZFFM", "num = " + numItemTrackElements);
            for (int i = 0; i < numItemTrackElements; i++) {
                Log.d("ParserPageZFFM", "i = " + i);
                Element trackFullName = searchContainer.select(TRACK_FULL_NAME).get(i);
                Element trackName = searchContainer.select(TRACK_NAME).get(i);
                Element trackArtistName = searchContainer.select(TRACK_NAME_ARTIST).get(i);
                Element trackTime = searchContainer.select(TRACK_TIME).get(i);
                Element itemTrackElement = itemTrackElements.get(i);
                String trackURL = itemTrackElement.absUrl(TRACK_URL);
                Log.d("ParserPageZFFM", "trackURL " + trackURL);

                Element tracksDescription = searchContainer.select("div [class=\"tracks-description\"]").get(i);
//                Element tracksInfo = tracksDescription.select("a [class=\"tracks-info\"]").get(i);
                Element tracksInfo = tracksDescription.child(2);
                String setTrack_info_url = tracksInfo.attr("href");
                Log.d("ParserPageZFFM", "setTrack_info_url " + setTrack_info_url);


                Uri uri;
                uri = Uri.parse(trackURL);

                Song song = new Song.Builder()
                                .setSongName(trackName.text())
                                .setSongId(i)
                                .setArtistName(trackArtistName.text())
                                .setArtistId(i)
                                .setAlbumName(trackTime.text())
                                .setAlbumId(i)
                                .setLocation(uri)
                                .setSongDuration(TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES))
                                .setYear(2016)
                                .setDateAdded(System.currentTimeMillis())
                                .setTrackNumber(i)
                                .setInLibrary(true)
                                .build();

                song.setTrack_info_url(setTrack_info_url);

                Log.d("ParserPageZFFM", "10 " + song.toString());
                objectTrackList.add(song);
            }
        }  catch (Exception e) {
        e.printStackTrace();
    }
            return objectTrackList;
    }

}
