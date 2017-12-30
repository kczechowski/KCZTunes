package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class NullState extends State {

    public NullState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        pane = new Pane();
        pane.setStyle("-fx-background-color: #222222;");
        Label label = new Label("Welcome");
        label.setTextFill(Paint.valueOf("#ffffff"));
        Text text = new Text("Add resources and build library or load library from File>Load library");
        text.setFill(Paint.valueOf("#ffffff"));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, text);
        pane.getChildren().addAll(vBox);
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
