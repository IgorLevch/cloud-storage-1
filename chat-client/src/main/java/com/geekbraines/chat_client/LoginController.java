/*
package com.geekbraines.chat_client;

import com.geekbraines.chat_common.AbstractMessage;

import com.geekbraines.chat_common.FileMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    public TextField field_login;

    @FXML
    public TextField field_password;

    @FXML
    public Label infoLabel;

    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = Connection.getConnection();
        Connection.start();
        Thread readThread = new Thread(() -> getMessage());    // в этом потоке мы ждем сообщение от сервака??
        readThread.setDaemon(true);
        readThread.start();
    }

    private void getMessage() {
        try {
            while (true) {
                AbstractMessage message = connection.readObject();
                if (message instanceof AuthMessage) {
                    AuthMessage authMessage = (AuthMessage) message;
                    if (authMessage.getLogin().equals(""))
                        updateInfoLabel("Неверные логин/пароль");
                    else {
                        switchScene();
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void signIn(){
        String login = field_login.getText();
        String pass = field_password.getText();
        if (login.equals("") || pass.equals("")){
            updateInfoLabel("Введите логин и пароль!");
            return;
        }
        AuthMessage message =  new AuthMessage(login, pass);
        connection.sendMsg(message);
    }

    public void switchScene() {
        Platform.runLater(()->{
            Parent root = null;
            try{
                FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("chat.fxml"));
                root = fxmlloader.load();
                Stage stage = (Stage) field_login.getScene().getWindow();
                Scene scene = new Scene(root,600,450);
                stage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateInfoLabel(String message) {
        Platform.runLater(()->{
            infoLabel.setText(message);
        });
    }

    public void register(ActionEvent actionEvent){
    }

    public void onEnterPressed (KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER)
            signIn();
    }


}
*/
