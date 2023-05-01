package com.geekbraines.chat_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class ChatApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("chat2.fxml"));
        primaryStage.setTitle("CloudBox");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> Connection.getConnection().stop());
    }
}



