package com.geekbraines.chat_common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String name;
    private byte[] bytes;

    public FileMessage(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public FileMessage(){}

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public FileMessage(Path path) throws IOException {
        name = path.getFileName().toString();
        bytes = Files.readAllBytes(path); //считывает файл и закидывает в память
    }

}
