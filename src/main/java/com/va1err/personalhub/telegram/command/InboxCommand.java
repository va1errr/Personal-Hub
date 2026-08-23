package com.va1err.personalhub.telegram.command;

import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageResponder;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.ui.ActiveInboxItemsKeyboardFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class InboxCommand implements Command {

    private static final Logger log =
        LoggerFactory.getLogger(InboxCommand.class);

    private final InboxService inboxService;
    private final MessageResponder messageResponder;
    private final ActiveInboxItemsKeyboardFactory activeInboxItemsKeyboardFactory;

    public InboxCommand(
        InboxService inboxService,
        MessageResponder messageResponder,
        ActiveInboxItemsKeyboardFactory activeInboxItemsKeyboardFactory
    ) {
        this.inboxService = inboxService;
        this.messageResponder = messageResponder;
        this.activeInboxItemsKeyboardFactory = activeInboxItemsKeyboardFactory;
    }

    @Override
    public String name() {
        return "/inbox";
    }

    @Override
    public void execute(Message message) {
        Slice<InboxItem> activeInbox = null;

        try {
            activeInbox =
                inboxService.getActiveInbox(
                    message.getFrom().getId(),
                    0,
                    5
                );
        } catch (RuntimeException e) {
            log.error(
                "Failed to get active inbox for Telegram user {}",
                message.getFrom().getId(),
                e
            );
        }
        messageResponder.respond(
            message,
            TelegramMessages.activeInbox(),
            activeInboxItemsKeyboardFactory.main(activeInbox, 0)
        );
    }

}
