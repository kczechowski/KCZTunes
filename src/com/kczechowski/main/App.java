package com.kczechowski.main;

import com.kczechowski.config.AppConfig;
import com.kczechowski.data.Library;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.handlers.EventManager;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.handlers.player.MusicPlayer;
import com.kczechowski.listeners.StateChangeListener;
import com.kczechowski.states.ArtistsListState;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;

public class App extends Application {

    private BorderPane borderPane;

    private MediaPlayer player;

    public static EventManager eventManager;
    public static Library library;
    private StateManager stateManager;
    private MusicPlayer musicPlayer;

    private String kcztunesPath = AppConfig.DEFAULT_RES_DIRECTORY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        eventManager = new EventManager();
        stateManager = new StateManager();
        musicPlayer = new MusicPlayer(this);
        musicPlayer.init();
        library = new Library();

        borderPane = new BorderPane();
        borderPane.setTop(getMenuBar());
        borderPane.setLeft(getSideMenu());
        borderPane.setCenter(getMain());
        borderPane.setBottom(getControlBar());
        borderPane.setDisable(true);

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

        library.addResourceDirectory(new File(AppConfig.DEFAULT_RES_DIRECTORY + "input" + File.separator));

        new Thread(()->{

            library.build(kcztunesPath);
            library.loadLibrary(Paths.get(kcztunesPath + "library" + File.separator));
            Platform.runLater(()->{
                borderPane.setDisable(false);
            });

        }).start();
    }

    public Pane getMain(){
        return stateManager.peekState().getContent();
    }

    public MenuBar getMenuBar(){
        MenuBar menuBar = new MenuBar();
        Menu btnFile = new Menu("File");
        Menu btnEdit = new Menu("Edit");
        Menu btnAbout = new Menu("About");
        menuBar.getMenus().addAll(btnFile, btnEdit, btnAbout);
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
        Label title = new Label("Name");
        Label artist = new Label("Artist");
        Label album = new Label("Album");
        smallSongInfoBox.getChildren().addAll(title, album, artist);

        HBox bigSongInfoBox = new HBox();
        bigSongInfoBox.setPrefWidth(200);
        bigSongInfoBox.getChildren().addAll(imageView, smallSongInfoBox);

        toolBar.getItems().addAll(bigSongInfoBox, btnPlay, btnPause);
        toolBar.setOrientation(Orientation.HORIZONTAL);
        if(!musicPlayer.isNullSong()){
            SongModel loadedSong = musicPlayer.getLoadedSong();
            title.setText(loadedSong.getSongName());
            artist.setText(loadedSong.getArtist().getArtistName());
            album.setText(loadedSong.getAlbum().getAlbumName());
            Image image = new Image(new ByteArrayInputStream(loadedSong.getAlbum().getAlbumImage()));
            imageView.setImage(image);
        }
        return toolBar;
    }

    public ScrollPane getSideMenu(){
        ScrollPane scrollPane = new ScrollPane();

        VBox vbox = new VBox();
        vbox.setPrefWidth(200);

        Text title = new Text("Library");
        title.setFont(Font.font(16));

        Button artistsBtn = new Button("Artists");
        artistsBtn.setOnAction(event -> stateManager.pushState(new ArtistsListState(stateManager)));

/*        Button albumsBtn = new Button("Albums");
        albumsBtn.setOnAction(event -> stateManager.pushState(new ArtistState(stateManager)));

        Button songsBtn = new Button("Songs");
        songsBtn.setOnAction(event -> stateManager.pushState(new SongsListState(stateManager)));*/

        vbox.getChildren().addAll(title, artistsBtn);

        scrollPane.setContent(vbox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private void setListeners(){
        App.eventManager.addStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChange() {
                borderPane.setCenter(getMain());
            }
        });
    }

}
