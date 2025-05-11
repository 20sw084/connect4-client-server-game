package com.connectfour.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class RegisterScreen {
    private Stage primaryStage;
    private Scene scene; // Hold the same scene
    private Socket socket;

    public RegisterScreen(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.scene = scene;
    }

    public Parent getView() {
        // Username
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setMaxWidth(200);

        // Password
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(200);

        // Register Button
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(200);

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(100);

        // Form
        VBox formBox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, registerButton);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(20));

        // Bottom
        HBox bottomBox = new HBox(loginButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(0, 20, 20, 20));

        // Layout
        BorderPane root = new BorderPane();
        root.setCenter(formBox);
        root.setBottom(bottomBox);

        // Action on Login
        loginButton.setOnAction(e -> {
            LoginScreen loginScreen = new LoginScreen(primaryStage, scene);
            scene.setRoot(loginScreen.getView());
        });

        registerButton.setOnAction(e -> {
            try {
                socket = new Socket("localhost", 12345); // Connect once
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());

                new Thread(() -> {
                    try {
                        while (true) {
                            String prompt = in.readUTF(); // Server message

                            if (prompt.equals("Enter your username:")) {
                                // We need to wait for user input, so run on JavaFX thread
                                javafx.application.Platform.runLater(() -> {
                                    // Nothing to do, user will press Register button again
                                });
                            } else if (prompt.equals("Username already taken. Please choose another.")) {
                                javafx.application.Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "Username already taken. Please try a different one.", ButtonType.OK);
                                    alert.showAndWait();
                                });
                            } else if (prompt.equals("Enter your password:")) {
                                javafx.application.Platform.runLater(() -> {
                                    String password = passwordField.getText();
                                    try {
                                        out.writeUTF(password);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            } else if (prompt.equals("Registration successful. Logging you in now...")) {
                                javafx.application.Platform.runLater(() -> {
                                    MenuScreen menuScreen = new MenuScreen(primaryStage, scene, usernameField.getText());
                                    scene.setRoot(menuScreen.getView());
                                });
                                break; // exit loop after successful registration
                            } else {
                                System.out.println("Unknown server message: " + prompt);
                            }
                        }
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();

                // Now when user clicks Register again:
                registerButton.setOnAction(ev -> {
                    try {
                        String username = usernameField.getText();
                        out.writeUTF(username);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        return root;
    }
}
