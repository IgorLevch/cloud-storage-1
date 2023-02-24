package com.geekbraines.chat_client;

import com.geekbraines.chat_common.AbstractMessage;
import com.geekbraines.chat_common.FileMessage;
import com.geekbraines.chat_common.FileRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class ChatController {

    @FXML
    public ListView <String> field_with_files;
    @FXML
    public TitledPane title_pane;





    public void getout(ActionEvent actionEvent) {
    }


    public void delete(ActionEvent actionEvent) {
    }

    public void add_file(ActionEvent actionEvent) {


    }

    public void unload_file(ActionEvent actionEvent) {
    }

    public void share_file(ActionEvent actionEvent) {
    }

    public void rename_file(ActionEvent actionEvent) {
    }




    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        field_with_files.setItems(FXCollections.observableArrayList());  // здесь вписал свою ЛистВьюху
        refreshLocalFilesList();
    }

      public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                field_with_files.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o ->
                        field_with_files.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    field_with_files.getItems().clear();
                    Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o ->
                            field_with_files.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }



}
