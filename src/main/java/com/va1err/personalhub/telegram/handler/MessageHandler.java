package com.va1err.personalhub.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface MessageHandler {

    void handle(Message message);

}
