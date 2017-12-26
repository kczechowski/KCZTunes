package com.kczechowski.main;

import com.kczechowski.config.AppConfig;
import com.kczechowski.handlers.EventManager;
import com.kczechowski.handlers.StateManager;
import com.kczechowski.listeners.MusicPlayerStatusChangeListener;
import com.kczechowski.listeners.StateChangeListener;
import com.kczechowski.states.AlbumsListState;
import com.kczechowski.states.ArtistsListState;
import com.kczechowski.states.SongsListState;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {

    private BorderPane borderPane;

    private MediaPlayer player;

    public static EventManager eventManager;
    private StateManager stateManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        eventManager = new EventManager();
        stateManager = new StateManager();

        borderPane = new BorderPane();
        borderPane.setTop(getMenuBar());
        borderPane.setLeft(getSideMenu());
        borderPane.setCenter(getMain());
        borderPane.setBottom(getControlBar());

        String uriString = new File(AppConfig.DEFAULT_RES_DIRECTORY + "library\\Arctic_Monkeys\\AM\\11.mp3").toURI().toString();
        Media media = new Media(uriString);
        player = new MediaPlayer(media);

        Scene scene = new Scene(borderPane, 800, 600);

        primaryStage.setTitle(AppConfig.DEFAULT_TITLE);
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
        Menu btnEdit = new Menu("Edit");
        Menu btnAbout = new Menu("About");
        menuBar.getMenus().addAll(btnFile, btnEdit, btnAbout);
        menuBar.setPadding(new Insets(0,0,0,0));
        return menuBar;
    }

    public ToolBar getControlBar(){
        ToolBar toolBar = new ToolBar();
        Button btnPlay = new Button("Play");
        btnPlay.setOnAction(event -> App.eventManager.onResume());
        Button btnPause = new Button("Pause");
        btnPause.setOnAction(event -> App.eventManager.onPause());
        Label title = new Label("Name");
        Label artist = new Label("Artist");

        toolBar.getItems().addAll(title, artist, btnPlay, btnPause);
        toolBar.setOrientation(Orientation.HORIZONTAL);
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

        Button albumsBtn = new Button("Albums");
        albumsBtn.setOnAction(event -> stateManager.pushState(new AlbumsListState(stateManager)));

        Button songsBtn = new Button("Songs");
        songsBtn.setOnAction(event -> stateManager.pushState(new SongsListState(stateManager)));

        vbox.getChildren().addAll(title, artistsBtn, albumsBtn, songsBtn);

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

        App.eventManager.addMusicPlayerStatusChangeListener(new MusicPlayerStatusChangeListener() {
            @Override
            public void onPause() {
                player.pause();
            }

            @Override
            public void onResume() {
                player.play();
            }
        });
    }

}
