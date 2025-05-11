package com.connectfour.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import javafx.application.Platform;

public class MenuScreen {
    private Stage primaryStage;
    private Scene scene;
    private String username;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String opponentUsername; 

    // Server connection details
    private static final String SERVER_ADDRESS = "localhost"; // Change to your server address
    private static final int SERVER_PORT = 12345; // The server's port

    public MenuScreen(Stage primaryStage, Scene scene, String username) {
        this.primaryStage = primaryStage;
        this.scene = scene;
        this.username = username;
    }

    public Parent getView() {
        // Welcome Label
        Label welcomeLabel = new Label("Welcome " + username);
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Buttons
        Button newGameButton = new Button("New Game");
        newGameButton.setPrefWidth(150);
        Button quitButton = new Button("Quit");
        quitButton.setPrefWidth(150);

        // Actions
        newGameButton.setOnAction(e -> {
            if (socket != null && !socket.isClosed()) {
                GameScreen gameScreen = new GameScreen(primaryStage, scene, username, opponentUsername, socket, input, output);
                scene.setRoot(gameScreen.getView());
            } else {
                System.out.println("Not connected to server.");
            }
        });

        quitButton.setOnAction(e -> {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            primaryStage.close();
        });

        // Layout for Buttons
        VBox buttonsBox = new VBox(10, newGameButton, quitButton);
        buttonsBox.setAlignment(Pos.CENTER);

        // Main Layout
        VBox mainBox = new VBox(20, welcomeLabel, buttonsBox);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(20));

        BorderPane root = new BorderPane();
        root.setCenter(mainBox);

        // Start a background thread to connect to the server
        new Thread(this::connectToServer).start();

        return root;
    }

    // Connect to server in a background thread
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server!");

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            // Send your username
            output.writeUTF(username);

            // Receive opponent username
            opponentUsername = input.readUTF();

            // Receive color assignment (optional - you can handle it later)
            String colorMessage = input.readUTF();
            System.out.println(colorMessage);

            // If you're player 1, you receive "Your move."
            String turnMessage = input.readUTF();
            System.out.println(turnMessage);

        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                System.out.println("Unable to connect to server.");
            });
        }
    }
}
