package com.example.warcaby;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StartScreen {
    private Scene menu;
    private Stage primaryStage;
    public StartScreen(Stage primaryStage){
        this.primaryStage = primaryStage;
        VBox sector = new VBox(20);
        sector.setAlignment(Pos.CENTER);
        Label menuText = new Label("Hello in checkers game!");
        menuText.setFont(Font.font("Verdana",30));
        menuText.setTextFill(Color.BLUEVIOLET);
        Button startButton = new Button("Start");
        startButton.setPrefSize(100,50);
        startButton.setFont(Font.font("Verdana",20));
        sector.setBackground(new Background(new BackgroundImage(new Image("images.png"),
                BackgroundRepeat.NO_REPEAT,  //powtarzanie w poziomie
                BackgroundRepeat.NO_REPEAT, //powtarzanie w pionie
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)))); //wypelnienie calego ekranu
        sector.getChildren().addAll(menuText,startButton);
        this.menu = new Scene(sector,650,650);
        changeSceneToGame(startButton,primaryStage);
    }

    public Scene getMenu(){
        return menu;
    }
    public static void changeSceneToGame(Button button,Stage primaryStage){
    button.setOnMouseClicked(event->{
        Game game = new Game(primaryStage);
        primaryStage.setScene(game.getSceneGame());
    });
    }
}
