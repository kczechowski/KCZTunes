package com.kczechowski.handlers;

import com.kczechowski.data.models.SongModel;
import com.kczechowski.listeners.LibraryStatusChangeListener;
import com.kczechowski.listeners.MusicPlayerStatusChangeListener;
import com.kczechowski.listeners.StateChangeListener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    public List<StateChangeListener> stateChangeListenerList;
    public List<MusicPlayerStatusChangeListener> musicPlayerStatusChangeListenerList;
    public List<LibraryStatusChangeListener> libraryStatusChangeListenerList;

    public EventManager() {
        stateChangeListenerList = new ArrayList<>();
        musicPlayerStatusChangeListenerList = new ArrayList<>();
        libraryStatusChangeListenerList = new ArrayList<>();
    }

    public void onLibraryStartedBuilding(){
        for(LibraryStatusChangeListener listener : libraryStatusChangeListenerList){
            listener.onStartedBuilding();
        }
    }

    public void onLibraryBuilt(){
        for(LibraryStatusChangeListener listener : libraryStatusChangeListenerList){
            listener.onBuilt();
        }
    }

    public void onLibraryStartedLoading(){
        for(LibraryStatusChangeListener listener : libraryStatusChangeListenerList){
            listener.onStartedLoading();
        }
    }

    public void onLibraryLoaded(){
        for(LibraryStatusChangeListener listener : libraryStatusChangeListenerList){
            listener.onLoaded();
        }
    }

    public void onLibraryFailedToLoad(){
        for(LibraryStatusChangeListener listener : libraryStatusChangeListenerList){
            listener.onFailedToLoad();
        }
    }

    public void onLibraryFailedToBuild(){
        for(LibraryStatusChangeListener listener : libraryStatusChangeListenerList){
            listener.onFailedToBuild();
        }
    }

    public void stateChanged(){
        for(StateChangeListener state : stateChangeListenerList){
            state.onStateChange();
        }
    }

    public void onSongPlayRequest(SongModel song){
        for(MusicPlayerStatusChangeListener listener : musicPlayerStatusChangeListenerList){
            listener.onSongPlayRequest(song);
        }
    }

    public void onPause(){
        for(MusicPlayerStatusChangeListener listener : musicPlayerStatusChangeListenerList){
            listener.onPause();
        }
    }

    public void onResume(){
        for(MusicPlayerStatusChangeListener listener : musicPlayerStatusChangeListenerList){
            listener.onResume();
        }
    }

    public void onMusicPlayerDispose(){
        for(MusicPlayerStatusChangeListener listener : musicPlayerStatusChangeListenerList){
            listener.onDispose();
        }
    }

    public void addStateChangeListener(StateChangeListener listener){
        stateChangeListenerList.add(listener);
    }

    public void removeStateChangeListener(StateChangeListener listener){
        stateChangeListenerList.remove(listener);
    }

    public void addMusicPlayerStatusChangeListener(MusicPlayerStatusChangeListener listener){
        musicPlayerStatusChangeListenerList.add(listener);
    }

    public void removeMusicPlayerStatusChangeListener(MusicPlayerStatusChangeListener listener){
        musicPlayerStatusChangeListenerList.remove(listener);
    }

    public void addLibraryStatusChangeListener(LibraryStatusChangeListener listener){
        libraryStatusChangeListenerList.add(listener);
    }

    public void removeLibraryStatusChangeListener(LibraryStatusChangeListener listener){
        libraryStatusChangeListenerList.remove(listener);
    }

}
