package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;

public class LoadLibraryState extends State {

    private ScrollPane scrollPane;

    public LoadLibraryState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/kczechowski/gui/LoadLibraryStatePane.fxml"));
        try {
            scrollPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Button loadLibraryButton = (Button) scrollPane.getContent().lookup("#LoadLibButton");
        loadLibraryButton.setOnAction(event -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File dir = dirChooser.showDialog(App.getPrimaryStage());
            if(dir != null){
                Thread loadLibraryThread = new Thread(()->{
                    if(App.getLibrary().isLoaded()) {
                        App.getMusicPlayer().dispose();
                        App.getLibrary().dispose();
                        stateManager.setState(new NullState(stateManager));
                    }
                    App.getLibrary().loadLibrary(dir.toPath());
                });
                loadLibraryThread.start();
            }
        });
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
