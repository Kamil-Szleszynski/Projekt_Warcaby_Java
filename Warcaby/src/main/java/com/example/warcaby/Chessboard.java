package com.example.warcaby;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Chessboard {
    private GridPane board;
    private Rectangle[][] fields;
    private Pawn[][] pawns;

    public Chessboard() {
        board = new GridPane();
        board.setAlignment(Pos.CENTER);
        fields = new Rectangle[8][8];
        pawns = new Pawn[8][8];
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                fields[i][j] = new Rectangle(75,75);
                if((i+j)%2!=0){
                    fields[i][j].setFill(Color.BROWN);
                }
                else{
                    fields[i][j].setFill(Color.WHITESMOKE);
                }
                fields[i][j].setStroke(Color.BLACK);
                board.add(fields[i][j],j,i);
            }
        }
        createPawns();
    }

    private void createPawns() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i + j) % 2 != 0) {
                    pawns[j][i] = new Pawn(Color.BLACK);
                    GridPane.setHalignment(pawns[j][i].getPawnView(), javafx.geometry.HPos.CENTER);
                    GridPane.setValignment(pawns[j][i].getPawnView(), javafx.geometry.VPos.CENTER);
                    pawns[j][i].getPawnView().setMouseTransparent(true);
                    board.add(pawns[j][i].getPawnView(),i,j);
                }
            }
            for (int j = 5; j<8; j++){
                if((i+j)%2 != 0){
                    pawns[j][i] = new Pawn(Color.WHITE);
                    GridPane.setHalignment(pawns[j][i].getPawnView(), javafx.geometry.HPos.CENTER);
                    GridPane.setValignment(pawns[j][i].getPawnView(), javafx.geometry.VPos.CENTER);
                    pawns[j][i].getPawnView().setMouseTransparent(true);
                    board.add(pawns[j][i].getPawnView(),i,j);
                } // pawns[wiersz][kolumna] a w metodzie getPawnView jest [kolumna][wiersz]
            }
        }
    }

    public GridPane getChessboard() {
        return board;
    }
    public Pawn[][] getPawns(){
        return pawns;
    }
    public void clickPosition(int row, int col){
        fields[row][col].setFill(Color.GOLD);
    } //podswietlenie wybranego pola
    public void changeToDefaultColor(int row, int col){ //ustawinie podstawowego koloru/usuniecie zanzaczenia
        if((row + col) % 2 != 0){
            fields[row][col].setFill(Color.BROWN);
        }
        else{
            fields[row][col].setFill(Color.WHITESMOKE);
        }
    }
    //podswietlenie mozliwych ruchow
    public void highlightPossibleMoves(int row, int col){
        fields[row][col].setFill(Color.GREEN);
    }
    public Rectangle[][] getFields(){
        return fields;
    }
}
