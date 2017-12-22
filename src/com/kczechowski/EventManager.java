package com.kczechowski;

public class EventManager {
    public StateChangeListener state;

    public void stateChanged(){
        state.onStateChange();
    }
}
