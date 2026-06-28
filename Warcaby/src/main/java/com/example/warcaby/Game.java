package com.example.warcaby;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game {
    private Stage primaryStage;
    private Scene sceneGame;
    private Chessboard chessboard;
    final int NONSELECTED = -1;
    private int selectedRow = NONSELECTED;  // -1 oznacza, że żaden pionek nie jest jeszcze wybrany
    private int selectedCol = NONSELECTED;
    private int acountWhite = 12;
    private int acountBlack = 12;
    private Color currentTurn = Color.WHITE;
    private Label textBlack;
    private Label textWhite;
    private boolean comboAtack = false;
    private int timeWhite = 120;
    private int timeBlack = 120;
    private Timeline timeline;
    public Game(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.chessboard = new Chessboard();
        GridPane board = chessboard.getChessboard();
        VBox sectors = new VBox(20);
        textBlack = new Label("Gracz 2  ------- You have " + acountBlack + " pawns -------" + " Tour timer " + timeBlack + "s");
        textWhite = new Label("Gracz 1 ------- Your turn ------- You have " + acountWhite + " pawns -------" + " Tour timer " + timeWhite + "s");
        sectors.getChildren().addAll(textBlack, board, textWhite);
        this.sceneGame = new Scene(sectors, 650, 700);
        ClickListening();
        useTimer();
    }

    private void useTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> { //co sekunde wykonuje sie kod w klamrach
            if (currentTurn == Color.WHITE) {
                timeWhite--;
                if (timeWhite <= 0) {
                    timeline.stop();
                    CanGoToWinScreen(); // Biały przekroczył czas -> Czarny wygrywa
                }
            } else {
                timeBlack--;
                if (timeBlack <= 0) {
                    timeline.stop();
                    CanGoToWinScreen(); // Czarny przekroczył czas -> Biały wygrywa
                }
            }
            updateLabel(); // Odświeżamy czas na ekranie co sekundę
        }));
        timeline.setCycleCount(Animation.INDEFINITE); // Zegar tyka w nieskonczonosc
        timeline.play(); //uruchomienie
    }

    public Scene getSceneGame() {
        return sceneGame;
    }

    private void ClickListening() {
        GridPane board = chessboard.getChessboard();
        Pawn[][] pawns = chessboard.getPawns();
        Rectangle[][] fields = chessboard.getFields(); // gdybyś dodał getter
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) { //ustawienie obslugi klikniecia na kazde pole
                final int row = i;
                final int col = j;
                fields[row][col].setOnMouseClicked(event -> handleClick(row, col));
            }
        }
    }

    private void handleClick(int row, int col) {
        Pawn[][] pawns = chessboard.getPawns();
        Pawn selectedPawn = pawns[row][col];
        GridPane board = chessboard.getChessboard();
        if (selectedRow == NONSELECTED && selectedCol == NONSELECTED) { //jesli nic nie jest zaznaczone
            if (selectedPawn != null && selectedPawn.getColor() == currentTurn) {
                choosePawn(row, col, pawns); //wybranie pionka
            }
        } else {
            if (selectedRow == row && selectedCol == col) {
                if (comboAtack) { //przymus bicia wielokrotnego
                    if (currentTurn == Color.WHITE)
                        textWhite.setText("Gracz 1 ------- You must beat next pawn -------" + " Tour timer " + timeWhite + "s");
                    else
                        textBlack.setText("Gracz 2 ------- You must beat next pawn -------" + " Tour timer " + timeBlack + "s");
                    return;
                }
                unclickPawn(pawns);
            } else if (isPossibleHit(row, col, pawns)) { //przymus bicia jesli jest mozliwe
                handleHit(row, col, pawns);
            } else if (!anyPossibleHits(pawns) && isCorrectMove(row, col, pawns)) { // ruch pionka
                clearPossibleMoves(pawns);
                movePawn(row, col, pawns);
            }
        }
    }

    private void handleHit(int row, int col, Pawn[][] pawns) {
        clearPossibleMoves(pawns);
        hitEnemy(row, col, pawns);
        if (isPossibleHitByThisPawn(row, col, pawns)) { //jesli jest mozliwe ponowne bicie przez tego
            comboAtack = true;                          //piona to flaga combo jest ustawiana na true
            selectedRow = row;                          //nie odklikuje pionka
            selectedCol = col;
            chessboard.clickPosition(selectedRow, selectedCol);
            showPossibleMoves(pawns);
            return;
        }
        comboAtack = false;
        selectedCol = NONSELECTED;
        selectedRow = NONSELECTED;
        currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        updateLabel();
    }

    private void unclickPawn(Pawn[][] pawns) {
        chessboard.changeToDefaultColor(selectedRow, selectedCol); //odklikniecie wybranego pionka
        selectedRow = NONSELECTED;
        selectedCol = NONSELECTED;
        clearPossibleMoves(pawns);
    }

    private void choosePawn(int row, int col, Pawn[][] pawns) {
        if (anyPossibleHits(pawns)) {
            int oldRow = selectedRow;
            int oldCol = selectedCol;
            selectedRow = row; //przypisanie globalnych zmiennych na chwile do kazdego pionka na polu i sprawdzenie czy jest bicie
            selectedCol = col; // sprawdzenie, czy jest potencjalne bicie
            boolean canThisPawnHit = false;
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (isPossibleHit(r, c, pawns)) canThisPawnHit = true;
                }
            }
            selectedRow = oldRow;
            selectedCol = oldCol;

            if (!canThisPawnHit) {
                communicatHit();
                return;
            }
        }
        selectedCol = col; //jesli nie ma przymusu bicia mozna wybrac pionek
        selectedRow = row;
        chessboard.clickPosition(selectedRow, selectedCol);
        showPossibleMoves(pawns);
    }

    private void movePawn(int row, int col, Pawn[][] pawns) {
        chessboard.changeToDefaultColor(selectedRow, selectedCol); //usuniecie podswietlenia
        pawns[row][col] = pawns[selectedRow][selectedCol];
        pawns[selectedRow][selectedCol] = null; //aktualizacja tablicy pawns
        GridPane.setColumnIndex(pawns[row][col].getPawnView(), col); //aktualizacja obrazu
        GridPane.setRowIndex(pawns[row][col].getPawnView(), row);
        selectedCol = NONSELECTED;
        selectedRow = NONSELECTED;
        Promoting(row, col, pawns);
        currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        updateLabel();
    }

    public void Promoting(int row, int col, Pawn[][] pawns) {
        if (currentTurn == Color.BLACK) {
            if (row == 7) {
                pawns[row][col].promoteToQueen();
            }
        } else {
            if (row == 0) {
                pawns[row][col].promoteToQueen();
            }
        }
    }

    private boolean isCorrectMove(int row, int col, Pawn[][] pawns) {
        if (pawns[row][col] != null)
            return false;
        if ((row + col) % 2 == 0) //biale pola
            return false;
        int translationY = selectedRow - row; // pionek musi isc zgodnie z kierunkiem koloru
        int tranlationX = Math.abs(selectedCol - col); //nie ma znaczenia czy lewo czy prawo
        if (pawns[selectedRow][selectedCol].isQueen()) {
            return isCorrectMoveQueen(row, col, pawns, tranlationX, translationY);
        } else {
            if (tranlationX != 1 && !pawns[selectedRow][selectedCol].isQueen())
                return false;
            if (translationY != 1 && pawns[selectedRow][selectedCol].getColor() == Color.WHITE)
                return false;
            if (translationY != -1 && pawns[selectedRow][selectedCol].getColor() == Color.BLACK)
                return false;
            return true;
        }
    }

    private boolean isCorrectMoveQueen(int row, int col, Pawn[][] pawns, int tranlationX, int translationY) {
        if (Math.abs(tranlationX) != Math.abs(translationY)) //kwadrat
            return false;
        int stepRow = (row > selectedRow) ? 1 : -1;
        int stepCol = (col > selectedCol) ? 1 : -1;
        int checkRow = selectedRow + stepRow; //wyznaczenie kierunku
        int checkCol = selectedCol + stepCol;
        while (checkRow != row && checkCol != col) {
            if (pawns[checkRow][checkCol] != null) { //sprawdzenie czy znajduje sie tutaj jakis pionek
                return false;
            }
            checkRow += stepRow;
            checkCol += stepCol;
        }
        return true;
    }

    private void showPossibleMoves(Pawn[][] pawns) {
        boolean possibleHit = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isPossibleHit(i, j, pawns)) {
                    chessboard.highlightPossibleMoves(i, j);
                    possibleHit = true;
                }
            }
        }
        if (possibleHit == false) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (isCorrectMove(i, j, pawns)) {
                        chessboard.highlightPossibleMoves(i, j);
                    }
                }
            }
        }
    }

    private void clearPossibleMoves(Pawn[][] pawns) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) {
                    chessboard.changeToDefaultColor(i, j);
                }
            }
        }
    }

    private void updateLabel() {
        if (currentTurn == Color.WHITE) {
            textWhite.setText("Gracz 1 ------- Your turn ------- You have " + acountWhite + " pawns -------" + " Tour timer " + timeWhite + "s");
            textBlack.setText("Gracz 2  ------- You have " + acountBlack + " pawns -------" + " Tour timer " + timeBlack + "s");
        } else {
            textBlack.setText("Gracz 2 ------- Your turn ------- You have " + acountBlack + " pawns -------" + " Tour timer " + timeBlack + "s");
            textWhite.setText("Gracz 1  ------- You have " + acountWhite + " pawns -------" + " Tour timer " + timeWhite + "s");
        }
    }
    private boolean isPossibleHit(int row, int col, Pawn[][] pawns) {
        if (pawns[row][col] != null)
            return false;
        if ((row + col) % 2 == 0)
            return false;
        int translationCol = selectedCol - col;
        int translationRow = selectedRow - row;
        if (pawns[selectedRow][selectedCol].isQueen()) { //logika damki
            return isPossibleHitQueen(row, col, pawns, translationCol, translationRow);
        }
        if (Math.abs(translationCol) != 2 || Math.abs(translationRow) != 2) //ruch o 2 pola
            return false;
        int middleCol = selectedCol - (translationCol / 2);
        int middleRow = selectedRow - (translationRow / 2); //wyznaczenie zbijanego piona
        if (pawns[middleRow][middleCol] == null)
            return false;
        Pawn enemy = pawns[middleRow][middleCol];
        if (enemy.getColor() == currentTurn)
            return false;
        return true;
    }

    private boolean isPossibleHitQueen(int row, int col, Pawn[][] pawns, int translationCol, int translationRow) {
        if (Math.abs(translationCol) != Math.abs(translationRow))
            return false;
        int quantityEnenmy = 0;
        int stepRow = (row > selectedRow) ? 1 : -1;
        int stepCol = (col > selectedCol) ? 1 : -1;
        int tempRow = selectedRow + stepRow;
        int tempCol = selectedCol + stepCol;
        while (tempRow != row && tempCol != col) {
            if (pawns[tempRow][tempCol] != null) {
                if (pawns[tempRow][tempCol].getColor() == currentTurn) {
                    return false;
                }
                quantityEnenmy++;
            }
            tempRow += stepRow;
            tempCol += stepCol;
        }
        if (quantityEnenmy != 1) //damka moze zbic naraz tylko 1 piona
            return false;
        return true;
    }

    private void hitEnemy(int row, int col, Pawn[][] pawns) {
        chessboard.changeToDefaultColor(selectedRow, selectedCol);
        int tempRow;
        int tempCol;
        if(pawns[selectedRow][selectedCol].isQueen()){
            int stepRow = (row > selectedRow) ? 1 : -1;
            int stepCol = (col > selectedCol) ? 1 : -1;
            tempRow = selectedRow + stepRow;
            tempCol = selectedCol + stepCol;
            while (tempRow != row && tempCol != col) {
                if (pawns[tempRow][tempCol] != null) {
                    break;
                }
                tempRow += stepRow;
                tempCol += stepCol;
            }
        }
        else {
            int translationCol = selectedCol - col;
            int translationRow = selectedRow - row;
            tempCol = selectedCol - (translationCol / 2);
            tempRow = selectedRow - (translationRow / 2);
        }
        deletePawn(row, col, pawns, tempRow, tempCol);
    }

    private void deletePawn(int row, int col, Pawn[][] pawns, int tempRow, int tempCol) {
        chessboard.getChessboard().getChildren().remove(pawns[tempRow][tempCol].getPawnView()); //usuniecie grafiki
        pawns[row][col] = pawns[selectedRow][selectedCol];
        pawns[selectedRow][selectedCol] = null;
        pawns[tempRow][tempCol] = null;
        GridPane.setColumnIndex(pawns[row][col].getPawnView(), col); //ustawienie piona
        GridPane.setRowIndex(pawns[row][col].getPawnView(), row);
        Promoting(row, col, pawns);
        UpdateStatsAfterHit();
        CanGoToWinScreen();
    }

    private void UpdateStatsAfterHit() {
        if (currentTurn == Color.WHITE)
            acountBlack--;
        else
            acountWhite--;
    }

    private boolean anyPossibleHits(Pawn[][] pawns) {
        int tempRow = selectedRow;
        int tempCol = selectedCol;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                selectedRow = i;
                selectedCol = j;
                if (pawns[i][j] != null && pawns[i][j].getColor() == currentTurn) {
                    for (int r = 0; r < 8; r++) {
                        for (int c = 0; c < 8; c++) {
                            if (isPossibleHit(r, c, pawns)) {
                                selectedRow = tempRow;
                                selectedCol = tempCol;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        selectedRow = tempRow;
        selectedCol = tempCol;
        return false;
    }

    void communicatHit() {
        if (currentTurn == Color.WHITE)
            textWhite.setText("Gracz 1 ------- You must hit -------" + " Tour timer " + timeWhite + "s");
        else
            textBlack.setText("Gracz 2 ------- You must hit -------" + " Tour timer " + timeBlack + "s");
    }

    boolean isPossibleHitByThisPawn(int row, int col, Pawn[][] pawns) { //czy z miejsca na ktore stanie pionek jest bicie
        int tempRow = selectedRow; //jest po to zeby nie odklikiwac pionka w combo
        int tempCol = selectedCol;
        selectedRow = row;
        selectedCol = col;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isPossibleHit(i, j, pawns)) {
                    selectedCol = tempCol;
                    selectedRow = tempRow;
                    return true;
                }
            }
        }
        return false;
    }

    private void CanGoToWinScreen() {
        if (acountBlack == 0 || timeBlack == 0) {
            timeline.stop();
            WinScreen winScreen = new WinScreen(primaryStage, Color.WHITE);
            primaryStage.setScene(winScreen.getWinScene());
        } else if (acountWhite == 0 || timeWhite == 0) {
            timeline.stop();
            WinScreen winScreen = new WinScreen(primaryStage, Color.BLACK);
            primaryStage.setScene(winScreen.getWinScene());
        }
    }
}