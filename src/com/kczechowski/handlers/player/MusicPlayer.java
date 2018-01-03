package com.kczechowski.handlers.player;

import com.kczechowski.data.models.SongModel;
import com.kczechowski.listeners.MusicPlayerStatusChangeEvent;
import com.kczechowski.listeners.MusicPlayerStatusChangeListener;
import com.kczechowski.main.App;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private MediaPlayer player;
//    private Media media;
    private boolean isPlaying = false;
    private boolean isNullSong = true;
    private SongModel songModel;
//    private File songFile;

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
                    player.dispose();
                }
                try {
                    SongModel song = event.getNewSong();
                    File songFile = new File(App.library.getLoadedLibraryPath() + song.getSongPath());

                    //copy file to temp file, so we can rebuild library w/o problems
                    File tempSongFile = File.createTempFile("temp", ".tmp");
                    tempSongFile.deleteOnExit();
                    FileUtils.copyFile(songFile, tempSongFile);

                    Media media = new Media(tempSongFile.toURI().toString());
                    player = new MediaPlayer(media);
                    isNullSong = false;
                    songModel = song;
                    onResume(null);//FIX
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPause(MusicPlayerStatusChangeEvent event) {
                if(!isNullSong){
                    player.pause();
                    isPlaying = false;
                }
            }

            @Override
            public void onResume(MusicPlayerStatusChangeEvent event) {
                if(!isNullSong){
                    player.play();
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
        if(player!=null)
            player.dispose();
        App.eventManager.fireMusicPlayerChangeEvent(new MusicPlayerStatusChangeEvent(this,MusicPlayerStatusChangeEvent.DISPOSE_REQUEST));
//        App.eventManager.onMusicPlayerDispose();
    }
}
