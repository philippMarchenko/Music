package com.devphill.music.model;


/**
 * Created by lerich on 16.11.17.
 */

public class Constants {

    /**
     * Конструктор
     */
    public Constants () {

    }

    /**
     * Название корневого каталога приложения для хранения пользовательских файлов
     */
    public static final String ROOT_FOLDER_PLAYER = "Dragon Player Music";
    public static final String FOLDER_DOWNLOAD_MUSIC = "Download";

    /**
     * Базовый каталог для поиска медиа файлов Для FileFinder startPath
     */
    private static final String EXTERNAL_MEMORY = "";
    public static final String INTERNAL_MEMORY = "/storage/sdcard1";



    /**
     * Возвращает адрес корневой папки внешнего накопителя
     * @return
     *//*
    public String getExternalMemory () {
        ConnectToExternal connectToExternal = new ConnectToExternal();
        //connectToExternal.pathToSD(EXTERNAL_MEMORY);
        return connectToExternal.getPathToSD(EXTERNAL_MEMORY);
    }

    /**
     * Форматы медиа файлов для поиска на устройстве
     */
    public static final String MUSIC_FORMAT_MP3 = ".*\\.mp3";
    public static final String MUSIC_FORMAT_WAV = ".*\\.wav";
    public static final String MUSIC_FORMAT_AAC = ".*\\.aac";
    public static final String MUSIC_FORMAT_WMA = ".*\\.wma";
    public static final String MUSIC_FORMAT_AMR = ".*\\.amr";
    public static final String MUSIC_FORMAT_OGG = ".*\\.ogg";
    public static final String MUSIC_FORMAT_MIDI = ".*\\.midi";

    /**
     * Адреса интернет ресурсов хранящих аудио файлы.
     * К адресу ZF_FM необходимо добавить слово поиска без пробела перед словом.
     * На данный момент для ZF_FM используется мобильная версия сайта
     */
    public static final String ZF_FM = "https://m.zf.fm/mp3/search?keywords=";

    /**
     *Адрес ресурса базы текстов к трекам. После адреса добавить поисковое слово.
     */
    public static final String LYRICSHARE = "http://lyricshare.net/ru/search/?q=";

    /**
     * Часть формирования запроса для последующих страниц поиска.
     * Добавлять без пробелов после поискового слова.
     * После элемента, без пробела указать номер желаймой страницы.
     */
    public  static final String ZF_FM_NEXT_PAGE = "&page=";

    /**
     * Описание браузеров под которые необходимо скачивать страницу
     */
    public  static final String MOZILLA = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0";

    /**
     * Обьекты для парсинга страницы поиска ZF_FM, для мобильной версии.
     * Контейнер поиска, содержащий все треки.
     */
    public  static final String TRACK_GENERIC_CONTAINER = "div [class=\"tracks\"]";
    /** Контейнер каждой отдельной песни. */
    public  static final String TRACK_ITEM_CONTAINER = "div [class=\"tracks-item\"]";
    /** Содержит общее название трека */
    public  static final String TRACK_FULL_NAME = "div [class=\"tracks-name\"]";
    /** Название трека */
    public  static final String TRACK_NAME = "div [class=\"tracks-name-title\"]";
    /** Исполнитель */
    public  static final String TRACK_NAME_ARTIST = "div [class=\"tracks-name-artist\"]";
    /** Ссылка на трек. Брать из тега TRACK_ITEM_CONTAINER */
    public  static final String TRACK_URL = "data-url"; //Заметка на получение URL url = url_title.absUrl("data-url");
    /** Продолжительность аудио записи */
    public  static final String TRACK_TIME = "div [class=\"tracks-time\"]";

    /**
     * Количество найденых композиций.
     */
    public  static final String NUMBER_OF_ITEMS = "div [class=\"artistLine-name\"]";

    /**
     * Обьекты для парсинга страницы поиска LYRICSHARE, для мобильной версии.
     */
    public  static final String TEXTS_ITEM = "div [class=\"td-item td-last\"]";
  /*  public  static final String  = "";
    public  static final String  = "";
    public  static final String  = "";
    public  static final String  = "";
    public  static final String  = "";*/

    //public  static final String  = "";

}
