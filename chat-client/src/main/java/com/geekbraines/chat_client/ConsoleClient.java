package com.geekbraines.chat_client;

import com.geekbraines.chat_common.StringMessage;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Scanner;


public class ConsoleClient {

    public static void main(String[] args) {
        NettyNet net = new NettyNet(System.out::println);  // когда объект пришел (см. ниже) - в консоль напечаталось
        Scanner in  = new Scanner(System.in);
        while(in.hasNextLine()) {
            String msg = in.nextLine();
            net.sendMessage(new StringMessage(msg, LocalDateTime.now()));  //послали такой объект в сеть
            // а сеть нам вернула этот же объект обратно
        }
    }
}


// нам нужно сеть поднимать в инициалайзере. Писать коллбек к тому сообщению , которое приходит. И в зависимости от
// того, что за сообщение пришло -- делать те или иные вещи.