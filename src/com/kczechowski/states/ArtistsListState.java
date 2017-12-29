package com.kczechowski.states;

import com.kczechowski.config.keys.BundleKeys;
import com.kczechowski.data.models.ArtistModel;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;

public class ArtistsListState extends State {

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

        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {

            ArtistModel artistModel = (ArtistModel) list.get(newValue.intValue());

            HashMap<String, Object> bundle = new HashMap<>();
            bundle.put(BundleKeys.ARTIST_ID, artistModel.getArtistID());

            ArtistState artistState = new ArtistState(stateManager);
            artistState.setBundle(bundle);
            stateManager.pushState(artistState);

        });


        VBox vBox = new VBox();
        vBox.getChildren().addAll(backButton, text, listView);

        pane.getChildren().addAll(vBox);


        new Thread(() -> {

            List<ArtistModel> artists = App.library.getAllArtists();
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
