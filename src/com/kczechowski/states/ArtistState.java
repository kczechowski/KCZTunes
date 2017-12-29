package com.kczechowski.states;

import com.kczechowski.config.keys.BundleKeys;
import com.kczechowski.data.models.AlbumModel;
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

public class ArtistState extends State {
    public ArtistState(StateManager stateManager) {
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

        ListView listView = new ListView();
        listView.setItems(list);

        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {

            AlbumModel albumModel = (AlbumModel) list.get(newValue.intValue());

            HashMap<String, Object> bundle = new HashMap<>();
            bundle.put(BundleKeys.ALBUM_ID, albumModel.getAlbumID());

            AlbumState albumState = new AlbumState(stateManager);
            albumState.setBundle(bundle);
            stateManager.pushState(albumState);
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(backButton, text, listView);

        pane.getChildren().addAll(vBox);

        new Thread(() -> {

            String artistID = (String) bundle.get(BundleKeys.ARTIST_ID);
            List<AlbumModel> albums = App.library.getAlbumsByArist(artistID);
            Platform.runLater(() -> {
                list.addAll(albums);
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
