package com.kczechowski.states;

import com.kczechowski.handlers.StateManager;
import com.kczechowski.main.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;

public class BuildLibraryState extends State {

    private ScrollPane scrollPane;
    private String outputPath;
    private ObservableList resources;

    public BuildLibraryState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void init() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/kczechowski/gui/BuildLibraryStatePane.fxml"));
        try {
            scrollPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextField outputPathField = (TextField) scrollPane.getContent().lookup("#OutputPathField");

        Button outputButton = (Button) scrollPane.getContent().lookup("#SetOutputButton");
        outputButton.setOnAction(event -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File dir = dirChooser.showDialog(App.getPrimaryStage());
            if(dir != null){
                outputPathField.setText(dir.getAbsolutePath());
                outputPath = dir.getAbsolutePath();
            }
        });

        resources = FXCollections.observableArrayList();
        ListView resourcesListView = (ListView) scrollPane.getContent().lookup("#ResourcesList");
        resourcesListView.setItems(resources);

        Button btnAddResources = (Button) scrollPane.getContent().lookup("#AddResButton");

        btnAddResources.setOnAction(event -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            File dir = dirChooser.showDialog(App.getPrimaryStage());
            if(dir != null){
                resources.add(dir);
            }
        });

        Button btnRemResources = (Button) scrollPane.getContent().lookup("#RemResButton");
        btnRemResources.setOnAction(event -> {
            if(!resourcesListView.getSelectionModel().isEmpty())
                resources.remove(resourcesListView.getSelectionModel().getSelectedIndex());
        });

        Button buildBtn = (Button) scrollPane.getContent().lookup("#BuildLibButton");
        buildBtn.setOnAction(e -> {
            if (resources.size() != 0 && outputPath != "") {
                Platform.runLater(() -> {
                    buildBtn.setDisable(true);
                });

                Thread buildingLibraryThread = new Thread(() -> {
                    //dispose objects if library was loaded previously
                    if (App.getLibrary().isLoaded()) {
                        App.getMusicPlayer().dispose();
                        App.getLibrary().dispose();
                    }

                    resources.forEach(r -> App.getLibrary().addResourceDirectory((File) r));

                    App.getLibrary().build(outputPath);

                    Platform.runLater(() -> {
                        buildBtn.setDisable(false);
                    });
                });
                buildingLibraryThread.start();
            } else if (resources.size() == 0) {
                App.showError(null, "No resources added.");
            } else if (outputPath == "") {
                App.showError(null, "No output path set.");
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
