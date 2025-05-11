package com.connectfour.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new Pane()); // temporary empty scene

        LoginScreen loginScreen = new LoginScreen(primaryStage, scene);
        scene.setRoot(loginScreen.getView());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
