package com.geekbraines.chat_client.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("CLoud Storage");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}



