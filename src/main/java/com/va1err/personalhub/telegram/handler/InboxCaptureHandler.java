package com.va1err.personalhub.telegram.handler;

import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.shared.exception.TgUserNotFoundException;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageResponder;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class InboxCaptureHandler implements MessageHandler {

    private static final Logger log =
        LoggerFactory.getLogger(InboxCaptureHandler.class);

    private final InboxService inboxService;
    private final MessageResponder messageResponder;

    public InboxCaptureHandler(
        InboxService inboxService,
        MessageResponder messageResponder
    ) {
        this.inboxService = inboxService;
        this.messageResponder = messageResponder;
    }

    @Override
    public void handle(Message message) {
        Long tgUserId = message.getFrom().getId();
        String content = message.getText();

        String responseText = capture(tgUserId, content);

        messageResponder.respond(message, responseText);
    }

    private String capture(Long tgUserId, String content) {
        try {
            inboxService.addInboxItem(tgUserId, content);

            return TelegramMessages.inboxItemSaved(content);
        } catch (TgUserNotFoundException exception) {
            return TelegramMessages.registrationRequired();
        } catch (RuntimeException exception) {
            log.error(
                "Inbox capture failed for Telegram user {}",
                tgUserId,
                exception
            );

            return TelegramMessages.systemUnavailable();
        }
    }

}
