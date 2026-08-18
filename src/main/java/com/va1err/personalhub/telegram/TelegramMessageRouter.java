package com.va1err.personalhub.telegram;

import com.va1err.personalhub.telegram.handler.CommandHandler;
import com.va1err.personalhub.telegram.handler.InboxCaptureHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class TelegramMessageRouter {

    private static final Logger log =
        LoggerFactory.getLogger(TelegramMessageRouter.class);

    private final CommandHandler commandHandler;
    private final InboxCaptureHandler inboxCaptureHandler;

    public TelegramMessageRouter(
        CommandHandler commandHandler,
        InboxCaptureHandler inboxCaptureHandler
    ) {
        this.commandHandler = commandHandler;
        this.inboxCaptureHandler = inboxCaptureHandler;
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
            inboxCaptureHandler.handle(message);
            return;
        }

        commandHandler.handle(message);
    }

}
