package com.devphill.music.ui.library.net_songs.give_pop_songs_and_radio;


//import io.realm.RealmObject;

/**
 * Объект радиостанций
 */
public class ObjectRadio {

    private String radioName;
    private String radioCity;
    private String imgURL;
    private String radioURL;

    public ObjectRadio() {

    }

    public ObjectRadio(
                       String radioName,
                       String radioCity,
                       String imgURL,
                       String radioURL) {

        this.radioName = radioName;
        this.radioCity = radioCity;
        this.imgURL = imgURL;
        this.radioURL = radioURL;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName() {
        this.radioName = radioName;
    }

    public String getRadioCity() {
        return radioCity;
    }

    public void setRadioCity() {
        this.radioCity = radioCity;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL() {
        this.imgURL = imgURL;
    }

    public String getRadioURL() {
        return radioURL;
    }

    public void setRadioURL() {
        this.radioURL = radioURL;
    }
}
