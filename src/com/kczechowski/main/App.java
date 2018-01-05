package com.kczechowski.main;

import com.kczechowski.config.AppConfig;
import com.kczechowski.data.Library;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.handlers.EventManager;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.handlers.player.MusicPlayer;
import com.kczechowski.listeners.*;
import com.kczechowski.states.ArtistsListState;
import com.kczechowski.states.BuildLibraryState;
import com.kczechowski.states.LoadLibraryState;
import com.kczechowski.states.NullState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;

public class App extends Application {

    public static EventManager eventManager;
    public static Library library;
    private StateManager stateManager;
    private static MusicPlayer musicPlayer;
    private static Stage primaryStage;
    private static Scene primaryScene;
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
        this.primaryStage = primaryStage;

        //Load app layout from file
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(this.getClass().getResource("/com/kczechowski/gui/MainLayout.fxml"));

        BorderPane borderPane = fxmlLoader.load();

        primaryScene = new Scene(borderPane, 800, 600);
        primaryStage.setTitle(AppConfig.DEFAULT_TITLE);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(primaryScene);
        primaryStage.show();

        setListeners();
        setButtonActions();
        updateLayout();


        eventManager.fireMusicPlayerChangeEvent(new MusicPlayerStatusChangeEvent(this, MusicPlayerStatusChangeEvent.RESUME));
    }

    private void updateLayout(){
        BorderPane borderPane = (BorderPane) primaryScene.lookup("#MainLayout");
        Pane centerPane = (Pane) borderPane.lookup("#MainLayoutCenter");
        centerPane.getChildren().clear();
        centerPane.getChildren().addAll(getTopStateContent());
    }

    private Node getTopStateContent(){
        return stateManager.peekState().getView();
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

    private void setButtonActions(){

        //sidemenu buttons
        ScrollPane scrollPane = (ScrollPane) primaryScene.lookup("#SideMenu");

        Button loadLibraryButton = (Button) scrollPane.lookup("#SideMenu_LoadLibraryButton");
        loadLibraryButton.setOnAction(event -> stateManager.setState(new LoadLibraryState(stateManager)));

        Button buildLibraryButton = (Button) scrollPane.lookup("#SideMenu_BuildLibraryButton");
        buildLibraryButton.setOnAction(event -> stateManager.setState(new BuildLibraryState(stateManager)));

        Button albumsButton = (Button) scrollPane.lookup("#SideMenu_LoadedLibraryAlbumsButton");
        albumsButton.setOnAction(event -> stateManager.pushState(new ArtistsListState(stateManager)));

        Button artistsButton = (Button) scrollPane.lookup("#SideMenu_LoadedLibraryArtistsButton");
        artistsButton.setOnAction(event -> stateManager.pushState(new ArtistsListState(stateManager)));

        Button songsButton = (Button) scrollPane.lookup("#SideMenu_LoadedLibrarySongsButton");
        songsButton.setOnAction(event -> stateManager.pushState(new ArtistsListState(stateManager)));

        //controlbar buttons
        ToolBar toolBar = (ToolBar) primaryScene.lookup("#ControlBarToolBar");
        Button resumeSongButton = (Button) toolBar.lookup("#ControlBarResumeButton");
        resumeSongButton.setOnAction(event -> App.eventManager.fireMusicPlayerChangeEvent(new MusicPlayerStatusChangeEvent(this, MusicPlayerStatusChangeEvent.RESUME)));
        Button pauseSongButton = (Button) toolBar.lookup("#ControlBarPauseButton");
        pauseSongButton.setOnAction(event -> App.eventManager.fireMusicPlayerChangeEvent(new MusicPlayerStatusChangeEvent(this, MusicPlayerStatusChangeEvent.PAUSE)));

    }

    private void setListeners(){
        App.eventManager.addStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChange(StateChangeEvent event) {
                Platform.runLater(()->{
                    updateLayout();
                });
            }
        });

        App.eventManager.addLibraryStatusChangeListener(new LibraryStatusChangeListener() {

            ScrollPane scrollPane = (ScrollPane) primaryScene.lookup("#SideMenu");
            Label label = (Label) scrollPane.lookup("#SideMenu_LoadedLibraryPath");
            AnchorPane libraryGroup = (AnchorPane) scrollPane.lookup("#SideMenu_LoadedLibraryContentGroup");

            @Override
            public void onStartedBuilding(LibraryStatusChangeEvent event) {
                Platform.runLater(()->{
                    dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setContentText("Building library. Please wait...");
                    dialog.showAndWait();
                });
            }

            @Override
            public void onFinishedBuilding(LibraryStatusChangeEvent event) {
                Platform.runLater(()->{
                    if(dialog!=null){
                        dialog.close();
                        dialog = null;
                    }
                });

                Thread loadLibraryThread = new Thread(()->{
                    if(library.isLoaded()) {
                        musicPlayer.dispose();
                        library.dispose();
                        stateManager.setState(new NullState(stateManager));
                    }
                    stateManager.setState(new NullState(stateManager));
                    library.loadLibrary(Paths.get( library.getLastBuiltLibraryPath() + File.separator + "library"));
                });
                loadLibraryThread.start();
            }

            @Override
            public void onStartedLoading(LibraryStatusChangeEvent event) {
                Platform.runLater(()->{
                    label.setText("(none)");
                    libraryGroup.setDisable(true);

                    dialog = new Alert(Alert.AlertType.INFORMATION);
                    dialog.setContentText("Loading library. Please wait...");
                    dialog.showAndWait();
                });

            }

            @Override
            public void onFinishedLoading(LibraryStatusChangeEvent event) {
                Platform.runLater(()->{
                    label.setText(library.getLoadedLibraryPath().toString());
                    libraryGroup.setDisable(false);
                    if(dialog!=null){
                        dialog.close();
                        dialog = null;
                    }
                    stateManager.setState(new NullState(stateManager));
                });
            }

            @Override
            public void onFailedToLoad(LibraryStatusChangeEvent event) {
                Platform.runLater(()->{
                    showError(event.getException());
                });
            }

            @Override
            public void onFailedToBuild(LibraryStatusChangeEvent event) {
                stateManager.setState(new NullState(stateManager));
                Platform.runLater(()->{
                    App.showError(event.getException());
                });
            }

        });

        App.eventManager.addMusicPlayerStatusChangeListener(new MusicPlayerStatusChangeListener() {

            ToolBar toolBar = (ToolBar) primaryScene.lookup("#ControlBarToolBar");
            ImageView albumImageView = (ImageView) toolBar.lookup("#ControlBarAlbumImageView");
            Label titleLabel = (Label) toolBar.lookup("#ControlBarSongName");
            Label albumLabel = (Label) toolBar.lookup("#ControlBarAlbumName");
            Label artistLabel = (Label) toolBar.lookup("#ControlBarArtistName");

            @Override
            public void onSongPlayRequest(MusicPlayerStatusChangeEvent e) {
                Platform.runLater(()->{
                    SongModel loadedSong = musicPlayer.getLoadedSong();
                    titleLabel.setText(loadedSong.getSongName());
                    artistLabel.setText(loadedSong.getArtist().getArtistName());
                    albumLabel.setText(loadedSong.getAlbum().getAlbumName());
                    Image image = new Image(new ByteArrayInputStream(loadedSong.getAlbum().getAlbumImage()));
                    albumImageView.setImage(image);
                });
            }

            @Override
            public void onPause(MusicPlayerStatusChangeEvent e) {

            }

            @Override
            public void onResume(MusicPlayerStatusChangeEvent e) {

            }

            @Override
            public void onDispose(MusicPlayerStatusChangeEvent e) {
                Platform.runLater(()->{
                    titleLabel.setText("");
                    artistLabel.setText("");
                    albumLabel.setText("");
                    albumImageView.setImage(null);
                });
            }
        });
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static Library getLibrary() {
        return library;
    }

    public static MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public static Scene getPrimaryScene() {
        return primaryScene;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
