package com.kczechowski.states;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kczechowski.data.models.ArtistsWrapper;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ArtistsListState extends State {

    List artists;

    public ArtistsListState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        Button backButton = new Button("<");
        backButton.setOnAction(event -> {
            stateManager.popState();
            App.eventManager.stateChanged();
        });
        Text text = new Text("Display all artists");

        ObservableList list = FXCollections.observableArrayList();

        ListView listView = new ListView();
        listView.setItems(list);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(backButton, text, listView);

        pane.getChildren().addAll(vBox);


        new Thread(() -> {
            try {
                JsonReader jsonReader = new JsonReader(new FileReader("res/library.json"));
                Gson gson = new Gson();
                ArtistsWrapper wrapper = gson.fromJson(jsonReader, ArtistsWrapper.class);
                artists = wrapper.getArtists();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                list.addAll(artists);
            });
        }).start();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update() {

    }
}
