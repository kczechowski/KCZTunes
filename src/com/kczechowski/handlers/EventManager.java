package com.kczechowski.handlers;

import com.kczechowski.listeners.StateChangeListener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    public List<StateChangeListener> stateChangeListenerList;

    public EventManager() {
        stateChangeListenerList = new ArrayList<StateChangeListener>();
    }

    public void stateChanged(){
        for(StateChangeListener state : stateChangeListenerList){
            state.onStateChange();
        }
    }

    public void addStateChangeListener(StateChangeListener listener){
        stateChangeListenerList.add(listener);
    }

    public void removeStateChangeListener(StateChangeListener listener){
        stateChangeListenerList.remove(listener);
    }

}
