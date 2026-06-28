package com.example.warcaby;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StartScreen startScreen = new StartScreen(primaryStage);
        primaryStage.setScene(startScreen.getMenu());
        primaryStage.setTitle("Warcaby");
        primaryStage.getIcons().add(new Image("icon.jpg"));
        primaryStage.show();
    }
}
