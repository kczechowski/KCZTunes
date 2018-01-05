package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class NullState extends State {

    ScrollPane scrollPane;

    public NullState(StateManager stateManager) {
        super(stateManager);
        scrollPane = new ScrollPane();
    }

    @Override
    public void init() {
        Label label = new Label("Welcome");
        Text text = new Text("Add resources and build library or load library from File>Load library");

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, text);
        scrollPane.setContent(vBox);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update() {

    }

    @Override
    public Node getView() {
        return scrollPane;
    }

}
