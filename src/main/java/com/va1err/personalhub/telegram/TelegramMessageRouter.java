package com.va1err.personalhub.telegram;

import com.va1err.personalhub.telegram.handler.CommandHandler;
import com.va1err.personalhub.telegram.handler.InboxCaptureHandler;
import com.va1err.personalhub.telegram.handler.TimezoneInputHandler;
import com.va1err.personalhub.telegram.state.TimezoneInputState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class TelegramMessageRouter {

    private final CommandHandler commandHandler;
    private final InboxCaptureHandler inboxCaptureHandler;
    private final TimezoneInputState timezoneInputState;
    private final TimezoneInputHandler timezoneInputHandler;

    public TelegramMessageRouter(
        CommandHandler commandHandler,
        InboxCaptureHandler inboxCaptureHandler,
        TimezoneInputState timezoneInputState,
        TimezoneInputHandler timezoneInputHandler
    ) {
        this.commandHandler = commandHandler;
        this.inboxCaptureHandler = inboxCaptureHandler;
        this.timezoneInputState = timezoneInputState;
        this.timezoneInputHandler = timezoneInputHandler;
    }

    public void route(Message message) {
        if (!message.isUserMessage() || message.getFrom() == null) {
            return;
        }

        if (timezoneInputState.isAwaiting(message.getFrom().getId())) {
            timezoneInputHandler.handle(message);
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
