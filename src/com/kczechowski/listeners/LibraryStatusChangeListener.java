package com.kczechowski.listeners;

public interface LibraryStatusChangeListener {
    void onStartedBuilding(LibraryStatusChangeEvent event);
    void onFinishedBuilding(LibraryStatusChangeEvent event);
    void onFailedToBuild(LibraryStatusChangeEvent event);
    void onStartedLoading(LibraryStatusChangeEvent event);
    void onFinishedLoading(LibraryStatusChangeEvent event);
    void onFailedToLoad(LibraryStatusChangeEvent event);
}
