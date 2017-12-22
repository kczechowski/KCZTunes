package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import javafx.scene.layout.Pane;

public class NullState extends State {

    public NullState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        pane = new Pane();
        pane.setStyle("-fx-background-color: #222222;");
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return "null";
    }
}
