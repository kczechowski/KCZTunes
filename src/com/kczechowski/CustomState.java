package com.kczechowski;

import com.kczechowski.main.App;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import javax.swing.*;

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
