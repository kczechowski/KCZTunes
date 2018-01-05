package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import com.sun.istack.internal.Nullable;
import javafx.scene.Node;

import java.util.HashMap;

public abstract class State {

    protected StateManager stateManager;
    @Nullable protected HashMap<String, Object> bundle;

    public State(StateManager stateManager){
        this.stateManager = stateManager;
    }

    public abstract void init();
    public abstract void dispose();
    public abstract void update();
    public abstract Node getView();

    public HashMap<String, Object> getBundle() {
        return bundle;
    }

    public void setBundle(HashMap<String, Object> bundle) {
        this.bundle = bundle;
    }
}
