package com.geekbraines.chat_client;

import com.geekbraines.chat_common.AbstractMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientMessagehandler extends SimpleChannelInboundHandler<AbstractMessage> {

        private final OnMessageReceived callback;

    public ClientMessagehandler(OnMessageReceived callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage abstractMessage)
            throws Exception {
        callback.onReceive(abstractMessage);
    }
}
