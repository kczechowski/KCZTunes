package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AlbumsListState extends State {
    public AlbumsListState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        Button backButton = new Button("<");
        backButton.setOnAction(event -> {
            stateManager.popState();
            App.eventManager.stateChanged();
        });
        Text text = new Text("Display all albums");

        ObservableList list = FXCollections.observableArrayList();
        list.addAll("Song1", "Song2", "Song3", "Song4", "Song5", "Song6", "Song7", "Song8", "Song9", "Song10",
                "Song11", "Song12", "Song13", "Song14", "Song15", "Song16", "Song17", "Song18", "Song19", "Song20", "Song21");

        ListView listView = new ListView();
        listView.setItems(list);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(backButton, text, listView);

        pane.getChildren().addAll(vBox);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update() {

    }
}
