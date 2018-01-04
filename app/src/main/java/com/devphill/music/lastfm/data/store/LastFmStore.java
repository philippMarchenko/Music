package com.devphill.music.lastfm.data.store;

import com.devphill.music.lastfm.model.LfmArtist;

import rx.Observable;

public interface LastFmStore {

    Observable<LfmArtist> getArtistInfo(String artistName);

}
