package com.kczechowski.main;

import com.kczechowski.config.AppConfig;
import com.kczechowski.data.Library;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.handlers.EventManager;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.handlers.player.MusicPlayer;
import com.kczechowski.listeners.LibraryStatusChangeListener;
import com.kczechowski.listeners.MusicPlayerStatusChangeListener;
import com.kczechowski.listeners.StateChangeListener;
import com.kczechowski.states.ArtistsListState;
import com.kczechowski.states.NullState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class App extends Application {

    private BorderPane borderPane;

    private MediaPlayer player;

    public static EventManager eventManager;
    public static Library library;
    private StateManager stateManager;
    private MusicPlayer musicPlayer;
    private DirectoryChooser directoryChooser;
    private Stage stage;
    private ObservableList resourcesList;
    private String outputPath = "";
    private Dialog dialog;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        eventManager = new EventManager();
        stateManager = new StateManager();
        musicPlayer = new MusicPlayer();
        musicPlayer.init();
        library = new Library();
        directoryChooser = new DirectoryChooser();
        this.stage = primaryStage;

        borderPane = new BorderPane();
        borderPane.setTop(getMenuBar());
        borderPane.setLeft(getSideMenu());
        borderPane.setCenter(getMain());
        borderPane.setBottom(getControlBar());

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setTitle(AppConfig.DEFAULT_TITLE);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();

        setListeners();
    }

    public Pane getMain(){
        return stateManager.peekState().getContent();
    }

    public MenuBar getMenuBar(){
        MenuBar menuBar = new MenuBar();

        Menu btnFile = new Menu("File");

        MenuItem btnLoadLibrary = new MenuItem("Load library");
        btnLoadLibrary.setOnAction(event -> {
            File dir = directoryChooser.showDialog(stage);
            if(dir != null){
                Thread loadLibraryThread = new Thread(()->{
                    if(library.isLoaded()) {
                        musicPlayer.dispose();
                        library.dispose();
                        stateManager.setState(new NullState(stateManager));
                    }
                    try {
                        library.loadLibrary(dir.toPath());
                    } catch (IOException e) {
                        Platform.runLater(()->{
                            showError(e);
                        });
                    }
                });
                loadLibraryThread.start();
            }
        });
        btnFile.getItems().addAll(btnLoadLibrary);

        Menu btnEdit = new Menu("Edit");

        Menu btnHelp = new Menu("Help");
        MenuItem btnInfo = new MenuItem("About");
        btnHelp.getItems().addAll(btnInfo);

        menuBar.getMenus().addAll(btnFile, btnEdit, btnHelp);
        menuBar.setPadding(new Insets(0,0,0,0));
        return menuBar;
    }

    public void update(){
        borderPane.setBottom(getControlBar());
    }

    public ToolBar getControlBar(){
        ToolBar toolBar = new ToolBar();
        Button btnPlay = new Button("Play");
        btnPlay.setOnAction(event -> App.eventManager.onResume());
        Button btnPause = new Button("Pause");
        btnPause.setOnAction(event -> App.eventManager.onPause());
        ImageView imageView = new ImageView();
        imageView.setFitWidth(64);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        VBox smallSongInfoBox = new VBox();
        Label title = new Label();
        Label artist = new Label();
        Label album = new Label();
        smallSongInfoBox.getChildren().addAll(title, album, artist);

        HBox bigSongInfoBox = new HBox();
        bigSongInfoBox.setPrefWidth(200);
        bigSongInfoBox.getChildren().addAll(imageView, smallSongInfoBox);

        toolBar.getItems().addAll(bigSongInfoBox, btnPlay, btnPause);
        toolBar.setOrientation(Orientation.HORIZONTAL);

        App.eventManager.addMusicPlayerStatusChangeListener(new MusicPlayerStatusChangeListener() {
            @Override
            public void onSongPlayRequest(SongModel song) {
                Platform.runLater(()->{
                    SongModel loadedSong = musicPlayer.getLoadedSong();
                    title.setText(loadedSong.getSongName());
                    artist.setText(loadedSong.getArtist().getArtistName());
                    album.setText(loadedSong.getAlbum().getAlbumName());
                    Image image = new Image(new ByteArrayInputStream(loadedSong.getAlbum().getAlbumImage()));
                    imageView.setImage(image);
                });
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onDispose() {
                Platform.runLater(()->{
                    title.setText("");
                    artist.setText("");
                    album.setText("");
                    imageView.setImage(null);
                });
            }
        });
        return toolBar;
    }

    public static void showError(Throwable e) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }

    public static void showError(Throwable e, String customMessage) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(customMessage);
            alert.showAndWait();
        });
    }

    public ScrollPane getSideMenu(){
        ScrollPane scrollPane = new ScrollPane();

        VBox vbox = new VBox();
        vbox.setPrefWidth(200);

        Label textFieldLabel = new Label("Output library path");

        TextField outputPathField = new TextField();
        outputPathField.setEditable(false);
        Button outputFileButton = new Button("Set");
        outputFileButton.setOnAction(event -> {
            File dir = directoryChooser.showDialog(stage);
            if(dir != null){
                outputPathField.setText(dir.getAbsolutePath());
                outputPath = dir.getAbsolutePath();
            }
        });

        HBox outputBox = new HBox();
        outputBox.getChildren().addAll(outputFileButton, outputPathField);

        Label resources = new Label("Resources");

        resourcesList = FXCollections.observableArrayList();
        ListView resourcesListView = new ListView();
        resourcesListView.setPrefHeight(100);
        resourcesListView.setItems(resourcesList);

        Button btnAddResources = new Button("Add");
        btnAddResources.setOnAction(event -> {
            File dir = directoryChooser.showDialog(stage);
            if(dir != null){
                resourcesList.add(dir);
            }
        });

        Button btnRemResources = new Button("Remove");
        btnRemResources.setOnAction(event -> {
            if(!resourcesListView.getSelectionModel().isEmpty())
                resourcesList.remove(resourcesListView.getSelectionModel().getSelectedIndex());
        });

        Button buildBtn = new Button("Build");
        buildBtn.setOnAction(e -> {
            if(resourcesList.size()==0) {
                showError(null, "No resources added.");
            }else {
                buildBtn.setDisable(true);
                Thread buildingLibraryThread = new Thread(() -> {
                    //dispose objects if library was loaded previously
                    if (library.isLoaded()) {
                        musicPlayer.dispose();
                        library.dispose();
                    }

                    resourcesList.forEach(r -> library.addResourceDirectory((File) r));
                    try {
                        if(outputPath != "")
                            library.build(outputPath);
                        else{
                            Platform.runLater(()->{
                                App.showError(null, "No output path set.");
                            });
                        }
                    } catch (IOException e1) {
                        Platform.runLater(()->{
                            App.showError(e1);
                            eventManager.onLibraryFailedToBuild();
                        });
                    }

                    Platform.runLater(() -> {
                        buildBtn.setDisable(false);
                    });
                });

                buildingLibraryThread.start();
            }
        });

        HBox resourcesBox = new HBox();
        resourcesBox.getChildren().addAll(btnAddResources, btnRemResources, buildBtn);

        vbox.getChildren().addAll(textFieldLabel, outputBox, resources, resourcesListView, resourcesBox);

        VBox libraryGroup = new VBox();

        Label libraryLabel = new Label("Loaded library");
        libraryLabel.setFont(Font.font(16));

        Label libraryPathLabel = new Label();
        libraryPathLabel.setFont(Font.font(10));

        Button artistsBtn = new Button("Artists");
        artistsBtn.setOnAction(event -> stateManager.pushState(new ArtistsListState(stateManager)));

        libraryGroup.getChildren().addAll(libraryLabel, libraryPathLabel, artistsBtn);
        libraryGroup.setVisible(false);
        App.eventManager.addLibraryStatusChangeListener(new LibraryStatusChangeListener() {
            @Override
            public void onStartedBuilding() {
                Platform.runLater(()->{
                    libraryGroup.setVisible(false);
                });
            }

            @Override
            public void onBuilt() {
            }

            @Override
            public void onStartedLoading() {
                Platform.runLater(()->{
                    libraryGroup.setVisible(false);
                });
            }

            @Override
            public void onLoaded() {
                Platform.runLater(()->{
                    libraryPathLabel.setText(library.getLoadedLibraryPath().toString());
                    libraryGroup.setVisible(true);
                });
            }

            @Override
            public void onFailedToBuild() {

            }

            @Override
            public void onFailedToLoad() {

            }
        });

        vbox.getChildren().addAll(libraryGroup);

        scrollPane.setContent(vbox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private void setListeners(){
        App.eventManager.addStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChange() {
                Platform.runLater(()->{
                    borderPane.setCenter(getMain());
                });
            }
        });

        App.eventManager.addLibraryStatusChangeListener(new LibraryStatusChangeListener() {
            @Override
            public void onStartedBuilding() {
//                stateManager.setState(new BuildLibraryState(stateManager));
                Platform.runLater(()->{
                    dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setContentText("Building library. Please wait...");
                    dialog.showAndWait();
                });
            }

            @Override
            public void onBuilt() {

                Platform.runLater(()->{
                    if(dialog!=null){
                        dialog.close();
                        dialog = null;
                    }
                });

                Thread loadLibraryThread = new Thread(()->{
                    stateManager.setState(new NullState(stateManager));
                    try {
                        library.loadLibrary(Paths.get( outputPath + File.separator + "library"));
                    } catch (IOException e) {
                        showError(e);
                    }
                });
                loadLibraryThread.start();
            }

            @Override
            public void onStartedLoading() {
                Platform.runLater(()->{
                    dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setContentText("Loading library. Please wait...");
                    dialog.showAndWait();
                });
            }

            @Override
            public void onLoaded() {
                Platform.runLater(()->{
                    if(dialog!=null){
                        dialog.close();
                        dialog = null;
                    }
                });
            }

            @Override
            public void onFailedToBuild() {
                stateManager.setState(new NullState(stateManager));
            }

            @Override
            public void onFailedToLoad() {

            }
        });
    }

}
