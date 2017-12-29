package com.kczechowski.listeners;

import com.kczechowski.data.models.SongModel;

public interface MusicPlayerStatusChangeListener {
    void onSongPlayRequest(SongModel song);
    void onPause();
    void onResume();
}
