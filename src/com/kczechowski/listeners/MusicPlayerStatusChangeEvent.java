package com.kczechowski.listeners;

import com.kczechowski.data.models.SongModel;

import java.util.EventObject;

public class MusicPlayerStatusChangeEvent extends EventObject {

    public final static int RESUME = 0;
    public final static int PAUSE = 1;
    public final static int SONG_PLAY_REQUEST = 2;
    public final static int DISPOSE_REQUEST = 3;

    private int type;
    private SongModel newSong;

    public MusicPlayerStatusChangeEvent(Object source, int type) {
        super(source);
        this.type = type;
    }

    public MusicPlayerStatusChangeEvent(Object source, int type, SongModel songModel) {
        super(source);
        this.type = type;
        this.newSong = songModel;
    }

    public int getType() {
        return type;
    }

    public SongModel getNewSong() {
        return newSong;
    }

    public void dispatch(MusicPlayerStatusChangeListener listener){
        switch(type){
            case RESUME:
                listener.onResume(this);
                break;
            case PAUSE:
                listener.onPause(this);
                break;
            case SONG_PLAY_REQUEST:
                listener.onSongPlayRequest(this);
                break;
            case DISPOSE_REQUEST:
                listener.onDispose(this);
                break;
        }
    }
}
