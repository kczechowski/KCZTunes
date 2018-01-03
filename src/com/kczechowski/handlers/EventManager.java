package com.kczechowski.handlers;

import com.kczechowski.listeners.*;

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

    public void fireMusicPlayerChangeEvent(MusicPlayerStatusChangeEvent event){
        for(MusicPlayerStatusChangeListener listener : musicPlayerStatusChangeListenerList){
            event.dispatch(listener);
        }
    }

    public void fireLibraryStatusChangeEvent(LibraryStatusChangeEvent event){
        for(LibraryStatusChangeListener listener : libraryStatusChangeListenerList){
            event.dispatch(listener);
        }
    }

    public void fireStateChangeEvent(StateChangeEvent event){
        for(StateChangeListener listener : stateChangeListenerList){
            event.dispatch(listener);
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
