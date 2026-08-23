package com.va1err.personalhub.telegram.handler;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageResponder;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class UnknownCommandHandler {

    private final MessageResponder messageResponder;

    public UnknownCommandHandler(
        MessageResponder messageResponder
    ) {
        this.messageResponder = messageResponder;
    }

    public void handle(Message message, String commandName) {
        messageResponder.respond(
            message,
            TelegramMessages.unknownCommand(commandName)
        );
    }
}
