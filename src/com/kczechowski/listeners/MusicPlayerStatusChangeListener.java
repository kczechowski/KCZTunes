package com.kczechowski.listeners;

public interface MusicPlayerStatusChangeListener {
    void onSongPlayRequest(MusicPlayerStatusChangeEvent e);
    void onPause(MusicPlayerStatusChangeEvent e);
    void onResume(MusicPlayerStatusChangeEvent e);
    void onDispose(MusicPlayerStatusChangeEvent e);
}
