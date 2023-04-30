package com.geekbraines.chat_common;

public class FileRequest extends AbstractMessage  {
    private String name;

    public String getName() {
        return name;
    }

    public FileRequest(String name) {
        this.name = name;
    }

}
