package com.geekbraines.chat_common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequest extends AbstractMessage  {
    private String fileName;

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }

    @Override
   public CommandType getType() {
        return CommandType.FILE_REQUEST;
    }
}
