package com.geekbraines.chat_client.handler;


import com.geekbraines.chat_client.auth.Authentication;
import com.geekbraines.chat_client.net.NettyNet;
import com.geekbraines.chat_common.handler.Command;
import com.geekbraines.chat_common.message.AbstractMessage;
import com.geekbraines.chat_common.message.AuthMessage;
import com.geekbraines.chat_common.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ClientMessagehandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private static ClientMessagehandler instance;
    private final NettyNet netty;
    private List<String> serverFiles;
    private String serverPath;
    private Path clientPath;

    private ClientMessagehandler(){
        netty = NettyNet.getInstance();
        this.serverFiles = new ArrayList<>();
    }

    public static ClientMessagehandler getInstance(){
        if(instance == null) {
            instance = new ClientMessagehandler();
            return instance;
        } else {
            return instance;
        }
    }

    // сохраняем путь к текущей папке клиента
    public void setClientPath(Path clientPath) {
        this.clientPath = clientPath;
    }

    // путь текущей папки на сервере
    public String getServerPath() {
        return serverPath;
    }

    // список файлов на сервере
    public List<String> getServerFiles(){
        return serverFiles;
    }

        //читаем  входящие сообщения от сервера
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage abstractMessage)
             {  // сообщение об авторизации
       if (abstractMessage.getClass() == AuthMessage.class) {
           auth(abstractMessage);
       }
                 // REFRESH отображенных списков файлов после операции на сервере
        if (abstractMessage.getClass() == Message.class &&((Message) abstractMessage).getCommand() == Command.REFRESH) {
            serverFiles = ((Message) abstractMessage).getListFiles();
            serverPath = ((Message) abstractMessage).getServerPath();
            log.debug("Handler Server Root {}  Server File List {}", serverPath, serverFiles);

        }

        // получение файла с сервера
          if(abstractMessage.getClass() == Message.class && ((Message) abstractMessage).getCommand() == Command.DOWNLOAD){
              receiveFile(abstractMessage);
          }
    }

    // получение и сохранение файла с сервера
    private void receiveFile(AbstractMessage abstractMessage) {
        String newFile = ((Message) abstractMessage).getFileName();
        try{
            File file = new File(clientPath + newFile.substring(newFile.lastIndexOf("/")));
            log.debug("New file is {}", file);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = ((Message) abstractMessage).getBuf();
            fos.write(buf);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // обработка сообщения об авторизации и сохранение его состояния
    private void auth(AbstractMessage abstractMessage){
        if(abstractMessage.getClass() == AuthMessage.class  && ((AuthMessage) abstractMessage).isAuth()){
            Authentication.setAuth(true);
        }
    }

}
