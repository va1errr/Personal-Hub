package com.va1err.personalhub.telegram.handler;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageDeleter;
import com.va1err.personalhub.telegram.message.MessageSender;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class UnknownCommandHandler implements MessageHandler {

    private static final Logger log =
        LoggerFactory.getLogger(UnknownCommandHandler.class);

    private final MessageSender messageSender;
    private final MessageDeleter messageDeleter;

    public UnknownCommandHandler(
        MessageSender messageSender,
        MessageDeleter messageDeleter
    ) {
        this.messageSender = messageSender;
        this.messageDeleter = messageDeleter;
    }

    @Override
    public void handle(Message message) {

    }

    public void handle(Message message, String commandName) {
        boolean messageSent = messageSender.send(
            message.getChatId(), TelegramMessages.unknownCommand(commandName)
        );

        if (messageSent) {
            messageDeleter.delete(message.getChatId(), message.getMessageId());
        }
    }
}
