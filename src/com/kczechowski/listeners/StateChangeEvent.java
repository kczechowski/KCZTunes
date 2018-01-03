package com.kczechowski.listeners;

import java.util.EventObject;

public class StateChangeEvent extends EventObject {

    public static final int STATE_CHANGED = 0;

    private int type;

    public StateChangeEvent(Object source, int type) {
        super(source);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void dispatch(StateChangeListener listener){
        switch (type){
            case STATE_CHANGED:
                listener.onStateChange(this);
                break;
        }
    }
}
