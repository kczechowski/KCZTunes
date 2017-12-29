package com.kczechowski.handlers.player;

import com.kczechowski.config.AppConfig;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.listeners.MusicPlayerStatusChangeListener;
import com.kczechowski.main.App;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class MusicPlayer {
    private MediaPlayer player;
    private Media media;
    private boolean isPlaying = false;
    private boolean isNullSong = true;

    public MusicPlayer() {

    }

    public void init(){
        setListeners();
    }

    private void setListeners(){
        App.eventManager.addMusicPlayerStatusChangeListener(new MusicPlayerStatusChangeListener() {
            @Override
            public void onSongPlayRequest(SongModel song) {
                if(!isNullSong)
                    player.dispose();
                String uriString = new File(AppConfig.DEFAULT_RES_DIRECTORY + song.getSongPath()).toURI().toString();
                Media media = new Media(uriString);
                player = new MediaPlayer(media);
                player.play();
                isPlaying = true;
                isNullSong = false;
            }

            @Override
            public void onPause() {
                if(!isNullSong){
                    player.pause();
                    isPlaying = false;
                }
            }

            @Override
            public void onResume() {
                if(!isNullSong){
                    player.play();
                    isPlaying = true;
                }
            }
        });
    }

}
