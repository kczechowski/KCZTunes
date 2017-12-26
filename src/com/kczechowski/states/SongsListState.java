package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SongsListState extends State {
    public SongsListState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        Button backButton = new Button("<");
        backButton.setOnAction(event -> {
            stateManager.popState();
            App.eventManager.stateChanged();
        });
        Text text = new Text("Display all songs");

        ObservableList list = FXCollections.observableArrayList();

        ListView listView = new ListView();
        listView.setItems(list);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(backButton, text, listView);

        pane.getChildren().addAll(vBox);

        new Thread(() -> {
            /*File file = new File(AppConfig.DEFAULT_RES_DIRECTORY + "\\AM\\");

            List files = FilesUtils.getFilesInFolder(file);

            Platform.runLater(() -> {
                list.addAll(files);
            });*/

        }).start();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update() {

    }
}
