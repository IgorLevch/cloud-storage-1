package com.geekbraines.chat_common;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Setter
public class FileMessage extends AbstractMessage {
    private String fileName;
    private byte[] bytes;


    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        bytes = Files.readAllBytes(path); //считывает файл и закидывает в память
    }


    @Override
    public CommandType getType() {
        return CommandType.FILE_RESPONSE;
    }
}
