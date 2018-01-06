package com.kczechowski.states;

import com.kczechowski.config.keys.BundleKeys;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.listeners.MusicPlayerStatusChangeEvent;
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
import java.util.List;

public class AlbumState extends State {

    private ScrollPane scrollPane;

    public AlbumState(StateManager stateManager) {
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
            SongModel songModel = (SongModel) list.get(newValue.intValue());
            App.eventManager.fireMusicPlayerChangeEvent(new MusicPlayerStatusChangeEvent(this, MusicPlayerStatusChangeEvent.SONG_PLAY_REQUEST, songModel));
        });

        listView.setOnMouseClicked(event -> {
            int l = listView.getSelectionModel().selectedIndexProperty().get();
            if(l>=0){
                SongModel songModel = (SongModel) list.get(l);
                App.eventManager.fireMusicPlayerChangeEvent(new MusicPlayerStatusChangeEvent(this, MusicPlayerStatusChangeEvent.SONG_PLAY_REQUEST, songModel));
            }
        });


        new Thread(() -> {

            String albumID = (String) bundle.get(BundleKeys.ALBUM_ID);
            List<SongModel> songs = App.library.getSongsByAlbum(albumID);
            Platform.runLater(() -> {
                list.addAll(songs);
                infoLabel.setText(songs.get(0).getAlbum().getAlbumName());
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
