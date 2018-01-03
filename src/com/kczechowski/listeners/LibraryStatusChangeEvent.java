package com.kczechowski.listeners;

import java.util.EventObject;

public class LibraryStatusChangeEvent extends EventObject{

    public static final int STARTED_BUILDING = 0;
    public static final int FINISHED_BUILDING = 1;
    public static final int FAILED_TO_BUILD = 2;
    public static final int STARTED_LOADING = 3;
    public static final int FINISHED_LOADING = 4;
    public static final int FAILED_TO_LOAD = 5;

    private int type;

    public LibraryStatusChangeEvent(Object source, int type) {
        super(source);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void dispatch(LibraryStatusChangeListener listener){
        switch(type){
            case STARTED_BUILDING:
                listener.onStartedBuilding(this);
                break;
            case FINISHED_BUILDING:
                listener.onFinishedBuilding(this);
                break;
            case FAILED_TO_BUILD:
                listener.onFailedToBuild(this);
                break;
            case STARTED_LOADING:
                listener.onStartedLoading(this);
                break;
            case FINISHED_LOADING:
                listener.onFinishedLoading(this);
                break;
            case FAILED_TO_LOAD:
                listener.onFailedToLoad(this);
                break;
        }
    }
}
