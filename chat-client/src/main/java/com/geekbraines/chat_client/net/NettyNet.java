package com.geekbraines.chat_client.net;

import com.geekbraines.chat_client.handler.ClientMessagehandler;
import com.geekbraines.chat_common.message.AbstractMessage;
import com.geekbraines.chat_common.message.AuthMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;


/**
 * Netty клиент Singleton
 */

@Slf4j
public class NettyNet {
    static NettyNet instance;
    SocketChannel channel;
   //private OnMessageReceived callback;

    private NettyNet() {
            new Thread(() ->{
            EventLoopGroup group = new NioEventLoopGroup();
            try{
            Bootstrap bootstrap = new Bootstrap();
           ChannelFuture future = bootstrap.channel(NioSocketChannel.class)
                    .group(group)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                        channel = ch;
                        ch.pipeline().addLast(
                                new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                ClientMessagehandler.getInstance()  // через коллбек этого хендлера будет осуществляться чтение
                                // в коллбеке будет зашита вся логика
                        );
                        }
                    }).connect("localhost", 8189).sync();
           future.channel().closeFuture().sync(); // block  --  это блокирующая операция, именно поэтому мы ее
                // запустили в отдельном потоке.
        } catch (Exception e) {
            log.error("e=", e);
            } finally{
        group.shutdownGracefully(); }
        }).start();
        }

        public static NettyNet getInstance(){
        if (instance == null) {
            instance = new NettyNet();
            return instance;
        } else {
            return instance;
        }
        }

    // отправка сообщения об авторизации на сервер
    public void sendAuth(AuthMessage message) {
        channel.writeAndFlush(message);
    }
    // метод отправки всех сообщений от клиента на сервер кроме авторизации
        public void sendMessage(AbstractMessage message){
        channel.writeAndFlush(message);
        }

}

