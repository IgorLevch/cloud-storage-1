package com.geekbraines.chat_client.ui;


import com.geekbraines.chat_client.auth.Authentication;
import com.geekbraines.chat_client.handler.ClientMessagehandler;
import com.geekbraines.chat_client.net.NettyNet;

import com.geekbraines.chat_common.handler.Command;
import com.geekbraines.chat_common.handler.Converter;
import com.geekbraines.chat_common.message.AuthMessage;
import com.geekbraines.chat_common.message.Message;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;


import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Основной обработчик графического интерфейса клиента
 */

@Slf4j
public class ChatController implements Initializable {

    private final long DELAY = 300L;
    @FXML
    public ListView<String> serverFileList;

    @FXML
    public ListView<String> clientFileList;
    @FXML
    public Label myComputerLabel;
    @FXML
    public Label cloudStorageLabel;

    public Button clientLevelUpButton;
    public Button serverLevelButton;
    private String selectedHomeFile;
    private String selectedServerFile;
    private NettyNet netty;
    private Path root;
    private boolean isSelectClientFile;
    private ClientMessagehandler handler;
    private Converter converter;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        netty = NettyNet.getInstance();
        root = Paths.get(System.getProperty("user.home"));
        handler = ClientMessagehandler.getInstance();
        converter = new Converter();

        // отправляем данные на авторизацию
        auth();
        // отображаем списки файлов на клиенте и сервере
        refreshClientFiles();
        refreshServerFiles();


        // двойной клик, навигация или открытие файла клиента
        clientFileList.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    if (isSelectClientFile) {
                        String navigatePath = root + "/" + converter.convertString(selectedHomeFile);
                        if (Files.isDirectory(Paths.get(navigatePath))) {

                            // проверка является ли папка доступной для пользователя
                            File folder = new File(navigatePath);
                            String[] folderFiles = folder.list();
                            if (folderFiles == null) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("error");
                                alert.setHeaderText("you do not have permission to access this folder");
                                alert.show();
                                return;
                            }

                            root = Paths.get(navigatePath);
                            refreshClientFiles();
                        } else {
                            try {
                                Desktop.getDesktop().open(new File(navigatePath));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        // двойной клик, навигация или открытие файла сервера
        serverFileList.setOnMouseClicked(event -> {
            isSelectClientFile = false;
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    if (selectedServerFile.contains("[Dir ]")) {
                        netty.sendMessage(new Message(selectedServerFile, Command.OPEN_IN));
                        refreshServerFiles();
                    } else if (selectedServerFile.contains("[file]")) {
                        Alert alert = new Alert((Alert.AlertType.CONFIRMATION));
                        alert.setTitle("open file from Cloud Storage");
                        String navigatePath = converter.convertString(selectedServerFile);
                        alert.setHeaderText("to open " + navigatePath + "you must first download it. Download it to My Computer?");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent()) {
                            if (result.get() == ButtonType.OK) {
                                netty.sendMessage(new Message(navigatePath, Command.DOWNLOAD));
                                //log.debug("Client file download");
                                refreshServerFiles();
                            }
                        } else if (result.get() == ButtonType.CANCEL) {
                            alert.close();
                        }
                    }
                }
            }
        });
    }


    // авторизация на сервере
    private void auth() {
        javafx.scene.control.Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Authentication");
        dialog.setHeaderText("Cloud Storage Authentication\nEnter user and password");
        dialog.setResizable(false);

        Label label1 = new Label("User: ");
        Label label2 = new Label("Password: ");
        Label label3 = new Label("New user?");
        javafx.scene.control.TextField user = new javafx.scene.control.TextField();
        PasswordField pass = new PasswordField();
        RadioButton isNew = new RadioButton();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(user, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(pass, 2, 2);
        grid.add(label3, 1, 3);
        grid.add(isNew, 2, 3);

        dialog.getDialogPane().setContent(grid);
        ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(ok);

        while (!Authentication.isAuth()) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                log.debug("User {} Pass {} isNew {}", user.getText(), pass.getText(), isNew.isSelected());
                netty.sendAuth(new AuthMessage(isNew.isSelected(), false, user.getText(), pass.getText()));

                // задержка на отправку и возврат авторизации из базы
                try {
                    Thread.sleep(DELAY * 5);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                if (Authentication.isAuth()) {
                    log.debug("Auth {}", Authentication.isAuth());
                    log.debug("Logged");
                    return;
                } else {
                    dialog.setHeaderText("login or password uncorrected\ntry again ...");
                    user.clear();
                    pass.clear();
                }
            }
        }
    }


    // обновление таблицы файлов сервера
    private void refreshServerFiles() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ObservableList<String> itemsServer = FXCollections.observableArrayList(handler.getServerFiles());
        serverFileList.setItems(itemsServer);
        serverFileList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        serverFileList.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener.Change<? extends String> change) ->
                {
                    ObservableList<String> oList = serverFileList.getSelectionModel().getSelectedItems();
                    selectedServerFile = oList.get(0);
                    isSelectClientFile = false;
                });
        updateServerPathLabel(handler.getServerPath());
    }

    // обновление таблицы файлов клиента
    private void refreshClientFiles() {
        File file = new File(root.toString());
        String[] fileList = file.list();
        List<String> files = Arrays.stream(fileList)
                .map(m -> new File(root.toString() + "\\" + m))
                .map(n -> {
                    if (n.isDirectory()) {
                        return "[Dir ]" + n;
                    } else {
                        return "[file]" + n + "\t\t" + converter.convertTime(n.lastModified()) + " " + converter.convertFileSize(n);
                    }
                })
                .sorted()
                .map(o -> o.substring(0, 6) + o.substring(o.lastIndexOf("\\") + 1))
                .peek(System.out::println)
                .collect(Collectors.toList());
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ObservableList<String> itemsClient = FXCollections.observableArrayList(files);
        clientFileList.setItems(itemsClient);
        clientFileList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        clientFileList.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener.Change<? extends String> change) ->
                {
                    ObservableList<String> oList = clientFileList.getSelectionModel().getSelectedItems();
                    selectedHomeFile = oList.get(0);
                    isSelectClientFile = true;
                });
        updateClientPathLabel(root);
    }


    // создание нового файла , папки
    public void create(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("new");
        dialog.setTitle("create new file");
        dialog.setHeaderText("create ?");
        ComboBox comboBox = new ComboBox<String>();
        ObservableList<String> oList = FXCollections.observableArrayList();
        oList.addAll("File on Client", "Directory on Client", "File on Server", "Directory on Server");
        comboBox.setItems(oList);
        comboBox.getSelectionModel().selectFirst();
        dialog.setGraphic(comboBox);
        Optional<String> result = dialog.showAndWait();
        String entered = "";
        if (result.isPresent()) {
            entered = result.get();
        }
        String createPath = root.toString() + "/" + entered;
        switch ((String) comboBox.getValue()) {
            case "File on Client":
                try {
                    Files.createFile(Paths.get(createPath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Directory on Client":
                try {
                    Files.createDirectory(Paths.get(createPath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "File on Server":
                netty.sendMessage(new Message(entered, Command.NEW_FILE));
                break;
            case "Directory on Server":
                netty.sendMessage(new Message(entered, Command.NEW_DIRECTORY));
                break;
        }
        refreshClientFiles();
        refreshServerFiles();
    }

    // отправка файла на сервер
    public void upload(ActionEvent event) {
        if (isSelectClientFile) {
            String uploadedFile = converter.convertString(selectedHomeFile);
            String uploadPath = root + "/" + uploadedFile;
            if (Files.isDirectory(Paths.get(uploadPath))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("upload file");
                alert.setHeaderText("you cannot upload directory, choose a file");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("upload file");
                alert.setHeaderText("upload " + uploadedFile + "?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        try {
                            FileInputStream fis = new FileInputStream(uploadPath);
                            byte[] buf = new byte[fis.available()];
                            fis.read(buf);
                            netty.sendMessage(new Message(uploadedFile, Command.UPLOAD, buf));
                            fis.close();
                            refreshServerFiles();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (result.get() == ButtonType.CANCEL) {
                        return;
                    }
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("upload file");
            alert.setHeaderText("you cannot upload file from server, choose a file from your computer");
            alert.showAndWait();
        }
    }

    // получение файла с сервера
    public void download(ActionEvent event) {
        if (!isSelectClientFile) {
            String downloadedFile = converter.convertString(selectedServerFile);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("download file");
            alert.setHeaderText("download " + downloadedFile + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    netty.sendMessage(new Message(downloadedFile, Command.DOWNLOAD));
                    handler.setClientPath(root);
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    refreshClientFiles();
                } else if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("download file");
            alert.setHeaderText("you have not selected a file to download");
            alert.showAndWait();
        }
    }

    // переименование файла или папки
    public void rename(ActionEvent event) throws IOException {
        if (isSelectClientFile) {
            String renamedFile = converter.convertString(selectedHomeFile);
            TextInputDialog dialog = new TextInputDialog(renamedFile);
            dialog.setTitle("rename file");
            dialog.setHeaderText("rename " + renamedFile + "?");
            Optional<String> result = dialog.showAndWait();
            String newNameFile = "";
            if (result.isPresent()) {
                newNameFile = result.get();
            }
            //log.debug(entered);
            Files.move(Paths.get(root.toString() + "/" + renamedFile), Paths.get(root.toString() + "/" + newNameFile));
        } else {
            String renamedFile = converter.convertString(selectedServerFile);
            TextInputDialog dialog = new TextInputDialog(renamedFile);
            dialog.setTitle("rename file");
            dialog.setHeaderText("rename " + renamedFile + "?");
            Optional<String> result = dialog.showAndWait();
            String newNameFile = "";
            if (result.isPresent()) {
                newNameFile = result.get();
            }
            netty.sendMessage(new Message(newNameFile, renamedFile, Command.RENAME));
        }
        refreshClientFiles();
        refreshServerFiles();
    }

    // удаление файла или папки
    public void delete(ActionEvent event) {
        if (isSelectClientFile) {
            String deletedFile = converter.convertString(selectedHomeFile);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("delete file");
            alert.setHeaderText("delete " + deletedFile + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    try {
                        Files.walk(Paths.get(root.toString() + "/" + deletedFile))
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        } else {
            String deletedFile = converter.convertString(selectedServerFile);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("delete file");
            alert.setHeaderText("delete " + deletedFile + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    netty.sendMessage(new Message(deletedFile, Command.DELETE));
                } else if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        }
        refreshClientFiles();
        refreshServerFiles();
    }

    // навигация на уровень выше на клиенте
    public void clientLevelUp(ActionEvent actionEvent) {
        root = Paths.get(root.toString().substring(0, root.toString().lastIndexOf("\\")));
        updateClientPathLabel(root);
        refreshClientFiles();
    }

    // навигация на уровень выше на сервере
    public void serverLevelUp(ActionEvent actionEvent) {
        String serverPath = handler.getServerPath().substring(0, handler.getServerPath().lastIndexOf("\\"));
        netty.sendMessage(new Message(serverPath, Command.OPEN_OUT));
        refreshServerFiles();
    }

    // обновить указатель пути клиента
    private void updateClientPathLabel(Path root) {
        clientLevelUpButton.setText("↑↑  " + root.toString());
    }

    // обновить указатель пути сервера
    private void updateServerPathLabel(String path) {
        serverLevelButton.setText("↑↑  " + path);
    }
}







