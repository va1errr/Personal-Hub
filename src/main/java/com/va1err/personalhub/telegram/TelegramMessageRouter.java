package com.va1err.personalhub.telegram;

import com.va1err.personalhub.telegram.handler.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class TelegramMessageRouter {

    private final CommandHandler commandHandler;

    public TelegramMessageRouter(
        CommandHandler commandHandler
    ) {
        this.commandHandler = commandHandler;
    }

    public void route(Message message) {
        if (!message.isUserMessage() || message.getFrom() == null) {
            return;
        }

        if (!message.hasText()) {
            return;
        }

        String text = message.getText().trim();

        if (!text.startsWith("/")) {
            return;
        }

        commandHandler.handle(message);
    }

}
