package com.geekbraines.chat_common;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FilesListResponse extends AbstractMessage{

    private List<String> files;

    public FilesListResponse(Path path) throws IOException {
        files = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList()); // это где мы будем брать наши файлы
    }

    @Override
   public CommandType getType() {
        return CommandType.FILES_LIST_RESPONSE;
    }
}
