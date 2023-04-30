package com.geekbraines.chat_client;

import com.geekbraines.chat_common.AbstractMessage;
import com.geekbraines.chat_common.FileMessage;

import com.geekbraines.chat_common.RequestMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    public ListView <String> Local_field_with_files;

    @FXML
    public ListView <String> Cloud_field_with_files;
    @FXML
    public Label localInfoLabel;
    @FXML
    public Label cloudInfoLabel;


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

    private Connection connection;
    private FileMessage fileMessage;
    private RequestMessage request;
    private File directory;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = Connection.getConnection();
        fileMessage = new FileMessage();
        request = new RequestMessage();
        directory = new File("chat-client/local");


        Thread readThread = new Thread(() -> getMessage());    // в этом потоке мы ждем сообщение от сервака??
        readThread.setDaemon(true);
        readThread.start();

        updateLocalHost();
        requestUpdate();
    }

    private void getMessage() {
        try {
            while (true) {
                AbstractMessage message = connection.readObject();
                if (message instanceof FileMessage) {
                    FileMessage file = (FileMessage) message;
                    FileOutputStream out = new FileOutputStream(new File(directory.getPath() + "/" + file.getName()));
                    out.write(file.getBytes());
                    out.close();
                    updateLocalHost();
                }
                    if (message instanceof RequestMessage){
                        RequestMessage request = (RequestMessage) message;
                        if (request.getType().equals(RequestMessage.REQUEST_UPDATE)){
                                updateCloudList(request);
                        }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }


    private void updateCloudList(RequestMessage request){
        Platform.runLater(()->{
            ObservableList<String> list =  FXCollections.observableArrayList();
            String[] filesList = request.getFiles();
            if (filesList == null) return;
            list.setAll(Arrays.asList(filesList));
            Cloud_field_with_files.setItems(list);

        });
    }

    //отправляем файлы в облако
    public void sendFileToServer(ActionEvent actionEvent) {
        ObservableList<String> selectedItems = Local_field_with_files.getSelectionModel().getSelectedItems();
        if (checkForSelection(selectedItems, localInfoLabel)) return;
        for (String fileName : selectedItems){
            try{
                byte[] bytes = Files.readAllBytes(Paths.get("chat-client/local"+fileName));
                fileMessage.setName(fileName);
                fileMessage.setBytes(bytes);
                connection.sendMsg(fileMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //подгружаем файлы из локального каталога
    public void updateLocalHost() {
        Platform.runLater(() ->{
            String [] list = directory.list();
            if(list == null) return;
            Local_field_with_files.getItems().setAll(Arrays.asList(list));
        });
    }


    // удалить локальный файл
    public void deleteLocalFile(ActionEvent actionEvent) {
        ObservableList<String> selectedItems = Local_field_with_files.getSelectionModel().getSelectedItems();
        for (String name: selectedItems
             ) { try{
                 Files.delete(Paths.get("chat-client/local" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        } updateLocalHost();
    }

    //запросить файлы с сервера
    public void requestFile(ActionEvent actionEvent) {
        ObservableList<String> selectedItems = Cloud_field_with_files.getSelectionModel().getSelectedItems();
        if (checkForSelection(selectedItems, cloudInfoLabel)) return;
        sendRequest(RequestMessage.REQUEST_FILE, selectedItems);
    }

    //запрос на удаление файлов с сервера
    public void requestDelete(ActionEvent actionEvent) {
        ObservableList<String> selectedItems = Cloud_field_with_files.getSelectionModel().getSelectedItems();
        if (checkForSelection(selectedItems, cloudInfoLabel)) return;
        sendRequest(RequestMessage.REQUEST_DELETE, selectedItems);
    }

    //запрос на обновление файлов на сервере
    public void requestUpdate() {
        sendRequest(RequestMessage.REQUEST_UPDATE, null);
    }

    private void sendRequest(String type, ObservableList<String> selectedItems){
            request.setType(type);
        if (selectedItems != null) {
            String[] files = new String[selectedItems.size()];
            request.setFiles(selectedItems.toArray(files));
        }
        connection.sendMsg(request);
    }

    private boolean checkForSelection(ObservableList<String> selectedItems, Label infoLabel){
        if(selectedItems.size() ==0) {
            infoLabel.setText("Ничего не выбрано!");
            return true;
        }
        return false;
    }

    //очистим выбранные при клике на пустоту
    public void updateLocalSelected(MouseEvent event){
        if (!event.getTarget().toString().startsWith("Text"))
            Local_field_with_files.getSelectionModel().clearSelection();
        else  localInfoLabel.setText("");
    }
    public void updateCloudSelected(MouseEvent event) {
        if (!event.getTarget().toString().startsWith("Text"))
            Cloud_field_with_files.getSelectionModel().clearSelection();
        else  cloudInfoLabel.setText("");

    }

}
