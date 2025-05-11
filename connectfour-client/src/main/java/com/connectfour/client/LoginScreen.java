package com.connectfour.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginScreen {
    private Stage primaryStage;
    private Scene scene;

    public LoginScreen(Stage primaryStage, Scene scene) {
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

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(200);

        // Register Button
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(100);

        // Form
        VBox formBox = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(20));

        // Bottom
        HBox bottomBox = new HBox(registerButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(0, 20, 20, 20));

        // Layout
        BorderPane root = new BorderPane();
        root.setCenter(formBox);
        root.setBottom(bottomBox);

        // Action on Register
        registerButton.setOnAction(e -> {
            RegisterScreen registerScreen = new RegisterScreen(primaryStage, scene);
            scene.setRoot(registerScreen.getView());
        });

        // Action on Login
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                MenuScreen menuScreen = new MenuScreen(primaryStage, scene, username);
                scene.setRoot(menuScreen.getView());
            } else {
                // Optionally, show an alert if username is empty
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a username.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        return root;
    }
}
