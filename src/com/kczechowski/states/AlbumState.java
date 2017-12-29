package com.kczechowski.states;

import com.kczechowski.config.keys.BundleKeys;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class AlbumState extends State {
    public AlbumState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        Button backButton = new Button("<");
        backButton.setOnAction(event -> {
            stateManager.popState();
            App.eventManager.stateChanged();
        });
        Label artistName = new Label();
        Label albumName = new Label();
        Text text = new Text("Display songs in album");

        ObservableList list = FXCollections.observableArrayList();

        ListView listView = new ListView();
        listView.setItems(list);

        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            SongModel songModel = (SongModel) list.get(newValue.intValue());
            App.eventManager.onSongPlayRequest(songModel);
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(backButton, artistName, albumName, text, listView);

        pane.getChildren().addAll(vBox);

        new Thread(() -> {

            String albumID = (String) bundle.get(BundleKeys.ALBUM_ID);
            List<SongModel> songs = App.library.getSongsByAlbum(albumID);
            Platform.runLater(() -> {
                list.addAll(songs);
                artistName.setText(songs.get(0).getArtist().getArtistName());
                albumName.setText(songs.get(0).getAlbum().getAlbumName());
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
