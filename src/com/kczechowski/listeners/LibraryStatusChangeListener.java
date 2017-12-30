package com.kczechowski.listeners;

public interface LibraryStatusChangeListener {
    void onStartedBuilding();
    void onBuilt();
    void onStartedLoading();
    void onLoaded();
    void onFailedToBuild();
    void onFailedToLoad();
}
