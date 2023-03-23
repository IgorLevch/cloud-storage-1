package com.geekbraines.chat_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class NettyServer {


    public static void main(String[] args) {

        //это экзекьюторы - классы, которые будут делать параллельное выполнение задач из очереди.
        EventLoopGroup auth = new NioEventLoopGroup(1); // легковесная группа экзекьюторов - 1 поток обрабатывающая
        EventLoopGroup worker = new NioEventLoopGroup(); // тяжеловесная группа экзекьюторов- весь тред пул под нее уходит.

        try {
            ServerBootstrap bootstrap = new ServerBootstrap(); // класс, с помощью коорого настраиваем конфигурацию сервера.
            ChannelFuture future = bootstrap.group(auth,worker)    // в конфигурации важно указать группу (проинициализировать)
                    // т.к. в метода return this, то пишем через точку.
                    .channel(NioServerSocketChannel.class)    // инициализируем канал
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {  // childHandler  срабатывает, когда подключается клиент
                        // эта функция для того, чтобы уметь обрабатывать входящие события на сервере
                            //внутрь этой функции мы должны передать обработчики
                        channel.pipeline().addLast(
                          new ObjectDecoder(ClassResolvers.cacheDisabled(null)),  // работает на вход -- объектные инкодер и декодер
                                //задача декодера - как только серриализуемый объект приходит частями, собрать его в декодере из набора байтов
                                // в единый объект и как только объект будет собран -- отдать его МесседжХендлеру
                          new ObjectEncoder(),     // работает на выход: когда отправляем объект в сеть, он пролетает через
                                // исходящий Энкодер и разбивается на набор байт.
                          new MessageHandler()  // это тоже вход
                        );
                        }
                    }).bind(8189).sync();
            log.debug("Server started ...");
            future.channel().closeFuture().sync(); // это блокирующая операция. ПОсле нее программа не выполняется.
        } catch (InterruptedException e) {
            log.error("e=",e);
        } finally {       // пишем сразу finally - т.к. это экзекьюторы,то их закрывать надо корректно.
        auth.shutdownGracefully();
        worker.shutdownGracefully();
    }

}}
