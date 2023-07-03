package com.geekbraines.chat_common.message;

import com.geekbraines.chat_common.handler.Command;
import lombok.Data;

import java.util.List;


@Data
public class Message extends AbstractMessage {

    private String fileName;
    private Command command;
    private byte[] buf;
    private String oldFileName;
    private List<String> listFiles;
    private String serverPath;


    public Message(Command command, List<String> listFiles, String serverPath) {
        this.command = command;
        this.listFiles = listFiles;
        this.serverPath = serverPath;
    }

    public Message(String fileName, Command command, byte[] buf) {
        this.fileName = fileName;
        this.command = command;
        this.buf = buf;
    }

    public Message(String fileName, Command command) {
        this.fileName = fileName;
        this.command = command;
    }

    public Message(String fileName, Command command, String oldFileName) {
        this.fileName = fileName;
        this.command = command;
        this.oldFileName = oldFileName;
    }

    public Message(String newNameFile, String renamedFile, Command rename) {
        super();
    }
}
