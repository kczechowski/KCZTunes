package com.kczechowski.states;

import com.kczechowski.config.keys.BundleKeys;
import com.kczechowski.data.models.ArtistModel;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ArtistsListState extends State {

    private ScrollPane scrollPane;

    public ArtistsListState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/kczechowski/gui/TextListStatePane.fxml"));
        try {
            scrollPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Label infoLabel = (Label) scrollPane.getContent().lookup("#InfoLabel");

        Button backButton = (Button) scrollPane.getContent().lookup("#BackButton");
        backButton.setOnAction(action ->{
            stateManager.popState();
        });

        ListView listView = (ListView) scrollPane.getContent().lookup("#ListView");

        ObservableList list = FXCollections.observableArrayList();
        listView.setItems(list);

        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {

            ArtistModel artistModel = (ArtistModel) list.get(newValue.intValue());

            HashMap<String, Object> bundle = new HashMap<>();
            bundle.put(BundleKeys.ARTIST_ID, artistModel.getArtistID());

            ArtistState artistState = new ArtistState(stateManager);
            artistState.setBundle(bundle);
            stateManager.pushState(artistState);

        });

        new Thread(() -> {

            List<ArtistModel> artists = App.library.getAllArtists();
            Platform.runLater(() -> {
                list.addAll(artists);
                infoLabel.setText("Artists");
            });

        }).start();

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
