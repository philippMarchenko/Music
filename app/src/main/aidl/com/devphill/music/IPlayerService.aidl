// IPlayerService.aidl
package com.devphill.music;

import com.devphill.music.data.store.ImmutablePreferenceStore;
import com.devphill.music.model.Song;
import com.devphill.music.player.PlayerState;
import com.devphill.music.player.RemoteEqualizer;
import com.devphill.music.player.transaction.ChunkHeader;
import com.devphill.music.player.transaction.TransactionToken;

interface IPlayerService {

    void stop();
    void skip();
    void previous();
    void togglePlay();
    void play();
    void pause();
    void setPreferences(in ImmutablePreferenceStore preferences);
    void setQueue(in List<Song> newQueue, int newPosition);
    void beginLargeQueueTransaction(in TransactionToken token);
    void sendQueueChunk(in ChunkHeader header, in List<Song> chunk);
    void endLargeQueueTransaction(boolean editQueue, int newPosition);
    void changeSong(int position);
    void editQueue(in List<Song> newQueue, int newPosition);
    void queueNext(in Song song);
    void queueNextList(in List<Song> songs);
    void queueLast(in Song song);
    void queueLastList(in List<Song> songs);
    void seekTo(int position);

    boolean isPlaying();
    Song getNowPlaying();
    List<Song> getQueue();
    int getQueuePosition();
    int getQueueSize();
    List<Song> getQueueChunk(int offset, int length);
    int getCurrentPosition();
    int getDuration();

    PlayerState getPlayerState();
    void restorePlayerState(in PlayerState state);

    int getMultiRepeatCount();
    void setMultiRepeatCount(int count);

    long getSleepTimerEndTime();
    void setSleepTimerEndTime(long timestampInMillis);

}
