package com.geekbraines.chat_server;

import com.geekbraines.chat_common.AbstractMessage;
import com.geekbraines.chat_common.AuthMessage;
import com.geekbraines.chat_common.FileMessage;
import com.geekbraines.chat_common.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


@Slf4j  // это чтобы печатать в лог
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage>  {

    private DAO dao;
    private File dir;
    private FileMessage fileMessage;
    private RequestMessage request;

    public MessageHandler(DAO dao){
        this.dao = dao;
        this.fileMessage = new FileMessage();
        this.request = new RequestMessage();
    }


    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected ...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //в этом методе, условно говоря, получаем от клиента сообщение и отдаем обратно - это когда тут был эхо-сервер)))

        log.debug("Received: {}", msg);
       // ctx.writeAndFlush(abstractMessage);  // просто эхо -сервер -- сообщение, которое получили - просто отдаем обратно.
        try {
            if (msg == null)
                return;
            if (msg instanceof AuthMessage) {
                AuthMessage message = (AuthMessage) msg;
                String user = message.getLogin();
                if (dao.checkLoginPassword(user, message.getPass())) {
                    System.out.println(user + "logged in");
                    ctx.write(message);
                    dir = new File("chat-server/local" + user);
                } else {
                    message.setLogin(" ");
                    ctx.write(message);
                }
                return;
            }

            if (msg instanceof RequestMessage) {
                RequestMessage message = (RequestMessage) msg;
                if (message.getType().equals(RequestMessage.REQUEST_UPDATE)) {
                    sendUpdateList(ctx);
                }
                if (message.getType().equals(RequestMessage.REQUEST_FILE)) {
                    sendFiles(ctx, message);
                }
                if (message.getType().equals(RequestMessage.REQUEST_DELETE)) {
                    String[] files = message.getFiles();
                    for (String file : files
                    ) {
                        Files.delete(Paths.get(dir.getPath() + "/" + file));
                    }
                    sendUpdateList(ctx);
                }
                return;
            }
            if (msg instanceof FileMessage) {
                createNewFile((FileMessage) msg);
                sendUpdateList(ctx);
                return;
            }
            System.out.println("Server received wrong object!");
        } finally {
            ReferenceCountUtil.release(msg);
        }}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {

    }

    private void createNewFile(FileMessage message) throws IOException{
        FileOutputStream out = new FileOutputStream(new File(dir, message.getName()));
        out.write(message.getBytes());
        out.close();
    }

        private void sendUpdateList(ChannelHandlerContext ctx){
        request.setType(RequestMessage.REQUEST_UPDATE);
        request.setFiles(dir.list()); // это отправили список файлов
       ctx.write(request);
        }

            private void sendFiles(ChannelHandlerContext ctx, RequestMessage message) {
            String[] files = message.getFiles();
                for (String file: files
                     ) {
                    try {
                        byte[] bytes = Files.readAllBytes(Paths.get(dir.getPath()+"/"+file)); //readAllBytes каждый раз
                        // создает новый байтовый массив в памяти
                        fileMessage.setName(file);
                        fileMessage.setBytes(bytes);
                        ctx.write(fileMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("Client was disconnected by himself");
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client disconnected ...");
    }
}

// тут всего 3 метода: sendUpdateList, sendFiles, createNewFile