package com.geekbraines.chat_common;

public class RequestMessage extends AbstractMessage {

    public static final String REQUEST_FILE = "file";
    public static final String REQUEST_UPDATE = "update";
    public static final String REQUEST_DELETE = "delete";

    private String type;
    private String[] files;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getFiles() {
        return files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }
}
