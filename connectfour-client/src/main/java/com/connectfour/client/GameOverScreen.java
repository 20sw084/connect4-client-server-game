package com.connectfour.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameOverScreen {
    private Stage primaryStage;
    private Scene scene;
    private boolean isWinner; // true if player won, false if lost

    public GameOverScreen(Stage primaryStage, Scene scene, boolean isWinner) {
        this.primaryStage = primaryStage;
        this.scene = scene;
        this.isWinner = isWinner;
    }

    public Parent getView() {
        // "Game Over" Label
        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // "You Won" / "You Lose" Label
        Label resultLabel = new Label(isWinner ? "You Won!" : "You Lose!");
        resultLabel.setStyle("-fx-font-size: 24px;");

        // Rematch Button
        Button rematchButton = new Button("Rematch");
        rematchButton.setPrefWidth(200);

        // New Game Button
        Button newGameButton = new Button("New Game");
        newGameButton.setPrefWidth(200);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(200);

        // Layout
        VBox layout = new VBox(20, gameOverLabel, resultLabel, rematchButton, newGameButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Actions (you can connect these later)
        rematchButton.setOnAction(e -> {
            // TODO: Handle rematch logic
            System.out.println("Rematch clicked");
        });

        newGameButton.setOnAction(e -> {
            // TODO: Handle new game logic
            System.out.println("New Game clicked");
        });

        backButton.setOnAction(e -> {
            MenuScreen menuScreen = new MenuScreen(primaryStage, scene, "Username"); // Pass a username properly
            scene.setRoot(menuScreen.getView());
        });

        return layout;
    }
}
