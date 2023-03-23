package com.geekbraines.chat_server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class StringHandler extends SimpleChannelInboundHandler<String> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected ...");
    } // метод вызывается, когда подключается клиент

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected ...");
    } // метод вызывается, когда выключается клиент.


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        //   ChannelHandlerContext ctx   - это неттиевский сетевой контекст
        // в контексте есть (вводим ctx , ставим точку и после точки нам выводит: ) пайплайн - очередь всех разработчиков. alloc - с которого можно выделить буфер.
        // executor - который будет нашу текущую задачу обрабатывать.
        // fireChannelRead - это пропихнуть соообщение в следующий инбаунд хендлер
        // writeAndFlush - это выпихнуть сообщение в АутбаундХендлер.
        log.debug("received: {}", s);
        ctx.writeAndFlush("Hello" + s);

    }
}
