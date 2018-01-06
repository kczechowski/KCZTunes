package com.kczechowski.states;

import com.kczechowski.config.keys.BundleKeys;
import com.kczechowski.data.models.AlbumModel;
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

public class ArtistState extends State {
    private ScrollPane scrollPane;
    public ArtistState(StateManager stateManager) {
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

            AlbumModel albumModel = (AlbumModel) list.get(newValue.intValue());

            HashMap<String, Object> bundle = new HashMap<>();
            bundle.put(BundleKeys.ALBUM_ID, albumModel.getAlbumID());

            AlbumState albumState = new AlbumState(stateManager);
            albumState.setBundle(bundle);
            stateManager.pushState(albumState);
        });

        listView.setOnMouseClicked(event -> {
            int l = listView.getSelectionModel().selectedIndexProperty().get();
            if(l>=0){
                AlbumModel albumModel = (AlbumModel) list.get(l);

                HashMap<String, Object> bundle = new HashMap<>();
                bundle.put(BundleKeys.ALBUM_ID, albumModel.getAlbumID());

                AlbumState albumState = new AlbumState(stateManager);
                albumState.setBundle(bundle);
                stateManager.pushState(albumState);
            }
        });

        new Thread(() -> {

            String artistID = (String) bundle.get(BundleKeys.ARTIST_ID);
            List<AlbumModel> albums = App.library.getAlbumsByArist(artistID);
            Platform.runLater(() -> {
                list.addAll(albums);
                infoLabel.setText(App.library.getArtistById(artistID).getArtistName());
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
