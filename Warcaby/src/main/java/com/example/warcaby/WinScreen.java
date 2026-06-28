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

public class WinScreen {
    private Stage primaryStage;
    private Scene winScene;
    private Color winner;
    public WinScreen(Stage primaryStage, Color winner){
        this.primaryStage = primaryStage;
        this.winner = winner;
        VBox sector = new VBox(20);
        sector.setAlignment(Pos.CENTER);
        Label menuText = new Label();
        setWinnerText(winner, menuText);
        menuText.setFont(Font.font("Verdana",40));
        menuText.setTextFill(Color.BLUEVIOLET);
        Button PlayAgainButton = new Button("Play again");
        PlayAgainButton.setPrefSize(200,50);
        PlayAgainButton.setFont(Font.font("Verdana",20));
        sector.setBackground(new Background(new BackgroundImage(new Image("images.png"),
                BackgroundRepeat.NO_REPEAT,  //powtarzanie w poziomie
                BackgroundRepeat.NO_REPEAT, //powtarzanie w pionie
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)))); //wypelnienie calego ekranu
        sector.getChildren().addAll(menuText,PlayAgainButton);
        this.winScene = new Scene(sector,650,650);
        StartScreen.changeSceneToGame(PlayAgainButton,primaryStage);
    }

    private static void setWinnerText(Color winner, Label menuText) {
        if(winner == Color.WHITE)
            menuText.setText("White win");
        else
            menuText.setText("Black win");
    }

    public Scene getWinScene(){
        return winScene;
    }
}
