package com.geekbraines.chat_server;

import com.geekbraines.chat_common.AbstractMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j  // это чтобы печатать в лог
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage>  {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage abstractMessage) throws Exception {

        //в этом методе, условно говоря, получаем от клиента сообщение и отдаем обратно

        log.debug("Received: {}", abstractMessage);
        ctx.writeAndFlush(abstractMessage);  // просто эхо -сервер -- сообщение, которое получили - просто отдаем обратно.
    }
}
