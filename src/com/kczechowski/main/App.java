package com.kczechowski.main;

import com.kczechowski.config.AppConfig;
import com.kczechowski.states.CustomState;
import com.kczechowski.handlers.EventManager;
import com.kczechowski.listeners.StateChangeListener;
import com.kczechowski.handlers.StateManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {

    private BorderPane borderPane;

    public static EventManager eventManager;
    private StateManager stateManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stateManager = new StateManager();
        eventManager = new EventManager();

        borderPane = new BorderPane();
        borderPane.setTop(getMenuBar());
        borderPane.setLeft(getSideMenu());
        borderPane.setCenter(getMain());
        borderPane.setBottom(getControlBar());
        Scene scene = new Scene(borderPane, 800, 600);

        primaryStage.setTitle(AppConfig.DEFAULT_TITLE);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();

        App.eventManager.addStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChange() {
                borderPane.setCenter(getMain());
            }
        });
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
        Button btnPause = new Button("Pause");
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
//        vbox.setPadding(new Insets(10));
//        vbox.setSpacing(8);

        Text title = new Text("Library");
//        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setFont(Font.font(16));
        Button button1 = new Button("Albums");
        Button button2 = new Button("Artists");

        button1.setOnAction(event -> {
            stateManager.pushState(new CustomState(stateManager));
            borderPane.setCenter(getMain());
        });

        vbox.getChildren().addAll(title, button1, button2);

        scrollPane.setContent(vbox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

}
