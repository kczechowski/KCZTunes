package com.kczechowski;

import javafx.scene.layout.Pane;

public abstract class State {

    protected Pane pane;
    protected StateManager stateManager;

    public State(StateManager stateManager){
        pane = new Pane();
        this.stateManager = stateManager;
    }

    public abstract void init();
    public abstract void dispose();
    public abstract void update();
    public Pane getContent(){
        return pane;
    }

}
