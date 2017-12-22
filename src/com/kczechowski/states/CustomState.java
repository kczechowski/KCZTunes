package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.scene.control.Button;

public class CustomState extends State {

    public CustomState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        pane.setStyle("-fx-background-color: #ffffff;");
        Button backButton = new Button("<");
        backButton.setOnAction(event -> {
           stateManager.popState();
           App.eventManager.stateChanged();
        });
        pane.getChildren().add(backButton);
    }

    @Override
    public void dispose() {
        pane.setDisable(true);
        pane = null;
    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return "custom";
    }
}
