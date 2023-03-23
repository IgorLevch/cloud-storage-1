package com.geekbraines.chat_client;

import com.geekbraines.chat_common.AbstractMessage;

public interface OnMessageReceived {
    void onReceive(AbstractMessage msg);

}
