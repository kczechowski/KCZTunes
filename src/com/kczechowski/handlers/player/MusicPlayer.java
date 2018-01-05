package com.kczechowski.handlers.player;

import com.kczechowski.data.models.SongModel;
import com.kczechowski.listeners.MusicPlayerStatusChangeEvent;
import com.kczechowski.listeners.MusicPlayerStatusChangeListener;
import com.kczechowski.main.App;
import jaco.mp3.player.MP3Player;
import javafx.scene.media.MediaPlayer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private MediaPlayer player;
    private MP3Player mp3Player;
    private boolean isPlaying = false;
    private boolean isNullSong = true;
    private SongModel songModel;

    public MusicPlayer() {
    }

    public void init(){
        setListeners();
    }

    private void setListeners(){
        App.eventManager.addMusicPlayerStatusChangeListener(new MusicPlayerStatusChangeListener() {

            @Override
            public void onSongPlayRequest(MusicPlayerStatusChangeEvent event) {
                if(!isNullSong) {
                    mp3Player.stop();
                }
                try {
                    SongModel song = event.getNewSong();
                    File songFile = new File(App.library.getLoadedLibraryPath() + song.getSongPath());

                    //copy file to temp file, so we can rebuild library w/o problems
                    File tempSongFile = File.createTempFile("temp", ".tmp");
                    tempSongFile.deleteOnExit();
                    FileUtils.copyFile(songFile, tempSongFile);

                    mp3Player = new MP3Player(tempSongFile);
                    mp3Player.play();

                    isNullSong = false;
                    songModel = song;
                    onResume(null);//FIX*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPause(MusicPlayerStatusChangeEvent event) {
                if(!isNullSong){
                    mp3Player.pause();
                    isPlaying = false;
                }
            }

            @Override
            public void onResume(MusicPlayerStatusChangeEvent event) {
                if(!isNullSong){
                    mp3Player.play();
                    isPlaying = true;
                }
            }

            @Override
            public void onDispose(MusicPlayerStatusChangeEvent e) {

            }
        });
    }

    public SongModel getLoadedSong(){
        return songModel;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isNullSong() {
        return isNullSong;
    }

    public void dispose(){
        if(mp3Player!=null){
            mp3Player.stop();
            mp3Player = null;
        }
        App.eventManager.fireMusicPlayerChangeEvent(new MusicPlayerStatusChangeEvent(this,MusicPlayerStatusChangeEvent.DISPOSE_REQUEST));
    }
}
