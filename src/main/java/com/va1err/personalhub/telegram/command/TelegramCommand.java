package com.va1err.personalhub.telegram.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface TelegramCommand {

    String name();

    void execute(Message message);

}
