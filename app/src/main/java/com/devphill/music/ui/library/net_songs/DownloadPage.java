package com.devphill.music.ui.library.net_songs;


import android.util.Log;

import com.devphill.music.model.Constants;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by lerich on 26.11.17.
 */

/**
 * Класс для получения страниц интернет ресурсов
 */
public class DownloadPage extends Constants {
    public String userAgent = null;
    private Document documentPage = null;

    public DownloadPage() {

    }

    /**
     * Метод возвращающий обьект страницы
     * @param url Адрес интернет ресурса
     * @param keywords Ключ для поиска. Если нет поискового слова, установить: ""
     * @return
     */
    public Document getPage(String url, String keywords) {
        Log.d("ParserPageZFFM", "1 1 " + url + keywords);
        return downloadPage(url, keywords);
    }

    /*
        Метод получения страниц интернет ресурсов
     */
    private Document downloadPage (String url, String keywords) {
        try {
            if (userAgent==null) userAgent = MOZILLA;
            Log.d("ParserPageZFFM", "1 2 " + url + keywords);
            documentPage = Jsoup.connect(url + keywords)
                    .userAgent(userAgent)
                    .get();
            Log.d("ParserPageZFFM", "1 3 " + documentPage);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return documentPage;
    }
}


