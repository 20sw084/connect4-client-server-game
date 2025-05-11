package com.connectfour.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random; // 🆕 Import Random
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GameScreen {
    private Stage primaryStage;
    private Scene scene;
    private String username;
    private String opponentUsername;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private String currentPlayer;
    private String player1 = "Player 1 (Red)";
    private String player2 = "Player 2 (Yellow)";
    private boolean gameEnded = false;
    private boolean myTurn = false;

    private String[][] board = new String[6][7];
    private Circle[][] boardCircles = new Circle[6][7];
    private GridPane boardGrid;

    public GameScreen(Stage primaryStage, Scene scene, String username, String opponentUsername, Socket socket, DataInputStream input, DataOutputStream output) {
        this.primaryStage = primaryStage;
        this.scene = scene;
        this.username = username;
        this.opponentUsername = opponentUsername;
        this.socket = socket;
        this.input = input;
        this.output = output;

        // Initialize the board
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = null;
            }
        }

        // 🆕 Randomly assign first player
        Random random = new Random();
        if (random.nextBoolean()) {
            currentPlayer = player1;
            myTurn = true;
        } else {
            currentPlayer = player2;
            myTurn = false;
        }
    }

    public Parent getView() {
        Label titleLabel = new Label("Connect Four");
        titleLabel.setFont(new Font(36));
        titleLabel.setPadding(new Insets(20, 0, 20, 0));

        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);

        boardGrid = new GridPane();
        boardGrid.setHgap(10);
        boardGrid.setVgap(10);
        boardGrid.setPadding(new Insets(20));
        boardGrid.setAlignment(Pos.CENTER);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle circle = new Circle(20);
                circle.setStyle("-fx-fill: white; -fx-stroke: black;");
                int finalCol = col;
                int finalRow = row;

                circle.setOnMouseClicked(event -> handleMove(finalRow, finalCol));

                boardGrid.add(circle, col, row);
                boardCircles[row][col] = circle;
            }
        }

        VBox gameArea = new VBox(10, titleBox, boardGrid);
        gameArea.setAlignment(Pos.TOP_CENTER);

        Label youLabel = new Label("You: " + username);
        Label opponentLabel = new Label();
        if (opponentUsername == null || opponentUsername.isEmpty()) {
            opponentLabel.setText("Opponent: Waiting for opponent...");
        } else {
            opponentLabel.setText("Opponent: " + opponentUsername);
        }
        Button resignButton = new Button("Resign");

        resignButton.setOnAction(e -> resignGame());

        VBox infoBox = new VBox(10, youLabel, opponentLabel, resignButton);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-border-color: black;");
        infoBox.setAlignment(Pos.TOP_LEFT);

        Label chatLabel = new Label("Chat");
        TextArea chatArea = new TextArea();
        chatArea.setPrefHeight(300);
        chatArea.setEditable(false);

        VBox chatBox = new VBox(5, chatLabel, chatArea);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-border-color: black;");

        VBox rightSide = new VBox(20, infoBox, chatBox);
        rightSide.setPrefWidth(250);
        rightSide.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setCenter(gameArea);
        root.setRight(rightSide);

        startListeningForMoves();

        return root;
    }

    private void handleMove(int row, int col) {
        if (gameEnded || !myTurn) return;

        int targetRow = -1;
        for (int r = 5; r >= 0; r--) {
            if (board[r][col] == null) {
                targetRow = r;
                break;
            }
        }

        if (targetRow == -1) {
            System.out.println("Column is full!");
            return;
        }

        board[targetRow][col] = currentPlayer;
        updateBoard();

        if (checkWin(targetRow, col)) {
            gameEnded = true;
            if (currentPlayer.equals(player1) && myTurn || currentPlayer.equals(player2) && myTurn) {
                showGameOverDialog("You Win!");
            } else {
                showGameOverDialog("You Lose!");
            }
        }
        else {
            currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
        }

        try {
            output.writeUTF("MOVE " + targetRow + " " + col);
            output.flush();
            myTurn = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showGameOverDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            GameOverScreen gameOverScreen = new GameOverScreen(primaryStage, scene, true); // true = won
            scene.setRoot(gameOverScreen.getView());
        });
    }

    private void updateBoard() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (board[row][col] == null) {
                    boardCircles[row][col].setStyle("-fx-fill: white; -fx-stroke: black;");
                } else if (board[row][col].equals(player1)) {
                    boardCircles[row][col].setStyle("-fx-fill: red; -fx-stroke: black;");
                } else if (board[row][col].equals(player2)) {
                    boardCircles[row][col].setStyle("-fx-fill: yellow; -fx-stroke: black;");
                }
            }
        }
    }

    private boolean checkWin(int row, int col) {
        // Check horizontal
        for (int c = Math.max(col - 3, 0); c <= Math.min(col, 3); c++) {
            if (board[row][c] != null && board[row][c].equals(board[row][col]) &&
                board[row][c + 1] != null && board[row][c + 1].equals(board[row][col]) &&
                board[row][c + 2] != null && board[row][c + 2].equals(board[row][col]) &&
                board[row][c + 3] != null && board[row][c + 3].equals(board[row][col])) {
                return true;
            }
        }

        // Check vertical
        for (int r = Math.max(row - 3, 0); r <= Math.min(row, 2); r++) {
            if (board[r][col] != null && board[r][col].equals(board[row][col]) &&
                board[r + 1][col] != null && board[r + 1][col].equals(board[row][col]) &&
                board[r + 2][col] != null && board[r + 2][col].equals(board[row][col]) &&
                board[r + 3][col] != null && board[r + 3][col].equals(board[row][col])) {
                return true;
            }
        }

        // Check diagonal (bottom-left to top-right)
        for (int r = Math.max(row - 3, 0), c = Math.max(col - 3, 0); r <= Math.min(row, 2) && c <= Math.min(col, 3); r++, c++) {
            if (board[r][c] != null && board[r][c].equals(board[row][col]) &&
                board[r + 1][c + 1] != null && board[r + 1][c + 1].equals(board[row][col]) &&
                board[r + 2][c + 2] != null && board[r + 2][c + 2].equals(board[row][col]) &&
                board[r + 3][c + 3] != null && board[r + 3][c + 3].equals(board[row][col])) {
                return true;
            }
        }

        // Check diagonal (top-left to bottom-right)
        for (int r = Math.min(row + 3, 5), c = Math.max(col - 3, 0); r >= Math.max(row, 3) && c <= Math.min(col, 3); r--, c++) {
            if (board[r][c] != null && board[r][c].equals(board[row][col]) &&
                board[r - 1][c + 1] != null && board[r - 1][c + 1].equals(board[row][col]) &&
                board[r - 2][c + 2] != null && board[r - 2][c + 2].equals(board[row][col]) &&
                board[r - 3][c + 3] != null && board[r - 3][c + 3].equals(board[row][col])) {
                return true;
            }
        }

        return false;
    }
    
    private void resignGame() {
        gameEnded = true;
        System.out.println(currentPlayer + " has resigned!");
    }

    private void startListeningForMoves() {
        new Thread(() -> {
            try {
                while (!gameEnded) {
                    String message = input.readUTF();
                    System.out.println("Received: " + message);

                    if (message.startsWith("MOVE")) {
                        String[] parts = message.split(" ");
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);

                        Platform.runLater(() -> {
                            if (board[row][col] == null) {
                                board[row][col] = currentPlayer;
                                updateBoard();
                                currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
                            }
                        });
                    } else if (message.equals("Your move.")) {
                        Platform.runLater(() -> {
                            myTurn = true;
                            System.out.println("It's your turn!");
                        });
                    } else if (message.startsWith("OPPONENT")) { // 🆕 Handle opponent username
                        String[] parts = message.split(" ", 2);
                        if (parts.length == 2) {
                            Platform.runLater(() -> {
                                opponentUsername = parts[1];
                                // Update opponent label on UI if needed (you'll need a reference to the label)
//                                opponentLabel.setText("Opponent: " + opponentUsername);
                            });
                        }
                    } else if (message.equals("WIN")) { // 🆕 Show winning dialog
                        gameEnded = true;
                        Platform.runLater(() -> showEndGameDialog(true));
                    } else if (message.equals("LOSE")) { // 🆕 Show losing dialog
                        gameEnded = true;
                        Platform.runLater(() -> showEndGameDialog(false));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void showEndGameDialog(boolean won) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(won ? "Congratulations! You won!" : "Sorry, you lost!");
        alert.showAndWait();
        
        GameOverScreen gameOverScreen = new GameOverScreen(primaryStage, scene, true); // true = won
        scene.setRoot(gameOverScreen.getView());
    }

}