package com.geekbraines.chat_client;


import com.geekbraines.chat_common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Connection { //бывш. Netty
    private static Socket socket;  // это сетевое соединение от клиента к серваку
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;
    private static final int MAX_OBJ_SIZE = 1024*1024*100; //100 mb
    private static Connection connection;

    private Connection() {}

    public static Connection getConnection() {
        return connection;
    }

    public static void start() {
        try {
            socket = new Socket("localhost", 8189);  // это соединение с серваком. Клиент может получать
            // сообщения от сервака, может их туда отправлять
            out = new ObjectEncoderOutputStream(socket.getOutputStream(), MAX_OBJ_SIZE);
            in = new ObjectDecoderInputStream(socket.getInputStream(), MAX_OBJ_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMsg(AbstractMessage msg) {  // позволяет отправить в сторону сервака какой-то объект
        try {
            out.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static AbstractMessage readObject() throws ClassNotFoundException, IOException { // получаем объект от сервака
        // исключения обязательно генерить в названии метода , а не через трай кетчи
        Object obj = in.readObject();
        return (AbstractMessage) obj;
    }
}
