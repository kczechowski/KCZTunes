package com.kczechowski.handlers;

import com.kczechowski.listeners.MusicPlayerStatusChangeListener;
import com.kczechowski.listeners.StateChangeListener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    public List<StateChangeListener> stateChangeListenerList;
    public List<MusicPlayerStatusChangeListener> musicPlayerStatusChangeListenerList;

    public EventManager() {
        stateChangeListenerList = new ArrayList<StateChangeListener>();
        musicPlayerStatusChangeListenerList = new ArrayList<MusicPlayerStatusChangeListener>();
    }

    public void stateChanged(){
        for(StateChangeListener state : stateChangeListenerList){
            state.onStateChange();
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

}
