package com.geekbraines.chat_client;

import com.geekbraines.chat_common.AbstractMessage;
import com.geekbraines.chat_common.FileMessage;

import com.geekbraines.chat_common.FilesListResponse;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChatController implements Initializable {

    private Path currentDir;
    @FXML
    public ListView<String> serverFiles;

    @FXML
    public ListView<String> clientFiles;
    @FXML
    public Label localInfoLabel;
    @FXML
    public Label cloudInfoLabel;

    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;


    public void getout(ActionEvent actionEvent) {
    }


    public void delete(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) throws IOException {  // здесь отпрваляем на сервер
        Path file = currentDir.resolve(clientFiles.getSelectionModel().getSelectedItem());
        if (!Files.isDirectory(file)) {
            os.writeObject(new FileMessage(file));
        }
    }

    public void download(ActionEvent actionEvent) {
    }

    public void share_file(ActionEvent actionEvent) {
    }

    public void rename_file(ActionEvent actionEvent) {
    }

    private Connection connection;
    private FileMessage fileMessage;
    private File directory;

    private void refreshView(List<String> files, ListView<String> view) {
        Platform.runLater(() -> { // чтоб дизайн не ломать
            view.getItems().clear();
            view.getItems().addAll(files);

        });
    }

    private List<String> getFilesInCurrentDir() throws IOException {
        return Files.list(currentDir)
                .map(p -> p.getFileName().toString() + " -- " + resolveType(p))  //  + " -- " + p.toFile().length()  -- это выводит длину файла
                .collect(Collectors.toList());
    }

    private String resolveType(Path p) {
        if(Files.isDirectory(p)) {
            return " [DIR]";
        } else{
            return " " + p.toFile().length() + "bytes";
        }
    }


    private void read() {

        try {
            while (true) {
                AbstractMessage msg = (AbstractMessage) is.readObject();
                switch (msg.getType()) {
                    case FILES_LIST_RESPONSE:  //нужно обновить файлики на сервере
                        refreshView(((FilesListResponse) msg).getFiles(), serverFiles);// получили мы данный список
                        // и выполнили функцию refresh
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            currentDir = Paths.get(System.getProperty("user.home"));
            refreshView(getFilesInCurrentDir(), clientFiles); // обновляем при старте клиентские файлы
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());// сначала аутпутстрим, потому что аутпутсрим регистрирует класс резолвер
            is = new ObjectDecoderInputStream(socket.getInputStream());// инпутсрим просит класс-резолвер, а его нет еще. Он регистируется на аутпутсриме
            Thread t = new Thread(this::read);
            t.setDaemon(true);
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

        /*  connection = Connection.getConnection();
        fileMessage = new FileMessage();
        request = new RequestMessage();
        directory = new File("chat-client/local");


        Thread readThread = new Thread(() -> getMessage());    // в этом потоке мы ждем сообщение от сервака??
        readThread.setDaemon(true);
        readThread.start();

        updateLocalHost();
        requestUpdate();
    }*/

  /*  }
    private void getMessage() {
        try {
            while (true) {
                AbstractMessage message = connection.readObject();
                if (message instanceof FileMessage) {
                    FileMessage file = (FileMessage) message;
                    FileOutputStream out = new FileOutputStream(new File(directory.getPath() + "/" + file.getFileName()));
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
            clientFiles.setItems(list);

        });
    }

    //отправляем файлы в облако
    public void sendFileToServer(ActionEvent actionEvent) {
        ObservableList<String> selectedItems = serverFiles.getSelectionModel().getSelectedItems();
        if (checkForSelection(selectedItems, localInfoLabel)) return;
        for (String fileName : selectedItems){
            try{
                byte[] bytes = Files.readAllBytes(Paths.get("chat-client/local"+fileName));
                fileMessage.setFileName(fileName);
                fileMessage.setBytes(bytes);
                connection.sendMsg(fileMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //подгружаем файлы из локального каталога
   *//* public void updateLocalHost() {  //   это аналог refreshServerView
        Platform.runLater(() ->{
            String [] list = directory.list();
            if(list == null) return;
            serverFiles.getItems().setAll(Arrays.asList(list));
        });
    }*//*


    // удалить локальный файл
    public void deleteLocalFile(ActionEvent actionEvent) {
        ObservableList<String> selectedItems = serverFiles.getSelectionModel().getSelectedItems();
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
        ObservableList<String> selectedItems = clientFiles.getSelectionModel().getSelectedItems();
        if (checkForSelection(selectedItems, cloudInfoLabel)) return;
        sendRequest(RequestMessage.REQUEST_FILE, selectedItems);
    }

    //запрос на удаление файлов с сервера
    public void requestDelete(ActionEvent actionEvent) {
        ObservableList<String> selectedItems = clientFiles.getSelectionModel().getSelectedItems();
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
            serverFiles.getSelectionModel().clearSelection();
        else  localInfoLabel.setText("");
    }
    public void updateCloudSelected(MouseEvent event) {
        if (!event.getTarget().toString().startsWith("Text"))
            clientFiles.getSelectionModel().clearSelection();
        else  cloudInfoLabel.setText("");

    }}

*/
