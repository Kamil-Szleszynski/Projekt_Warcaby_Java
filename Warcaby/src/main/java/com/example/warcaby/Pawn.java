package com.example.warcaby;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;



public class Pawn {
    private ImageView graphic;
    private Color color;
    private boolean isQueen;

    public Pawn(Color color){
        this.color = color;
        this.isQueen = false;
        if(color==Color.BLACK){
            this.graphic = new ImageView(new Image("black.png"));
        }
        else if(color==Color.WHITE){
            this.graphic = new ImageView(new Image("white.png"));
        }
    }
    public void promoteToQueen(){
        isQueen = true;
        if(color==Color.BLACK){
            this.graphic.setImage(new Image("black_queen.png"));
        }
        else if(color==Color.WHITE){
            this.graphic.setImage(new Image("white_queen.png"));
        }
    }
    public ImageView getPawnView(){
        return graphic;
    }
    public Color getColor(){
        return color;
    }
    public boolean isQueen(){
        return isQueen;
    }
}
