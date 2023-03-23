package com.geekbraines.chat_common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true) // т.к. мы наследники - икуалс и хеш-код запилит родителю.
@Data
@AllArgsConstructor
public class StringMessage extends AbstractMessage {
    private String content;
    private LocalDateTime time;
// сюда можно зашить имя файла, размер файла, байты файла.
}
