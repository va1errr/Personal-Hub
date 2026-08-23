package com.va1err.personalhub.telegram.callback.handler;

import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageEditor;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.ui.ActiveInboxItemsKeyboardFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@ConditionalOnTelegramEnabled
@Component
public class ActiveInboxNavigationCallbackHandler {

    private static final Logger log =
        LoggerFactory.getLogger(ActiveInboxNavigationCallbackHandler.class);

    private final MessageEditor messageEditor;
    private final ActiveInboxItemsKeyboardFactory activeInboxItemsKeyboardFactory;
    private final InboxService inboxService;

    public ActiveInboxNavigationCallbackHandler(
        MessageEditor messageEditor,
        ActiveInboxItemsKeyboardFactory activeInboxItemsKeyboardFactory,
        InboxService inboxService
    ) {
        this.messageEditor = messageEditor;
        this.activeInboxItemsKeyboardFactory = activeInboxItemsKeyboardFactory;
        this.inboxService = inboxService;
    }

    public void handle(Long tgUserId, Long chatId, Integer messageId, String callbackData) {
        if (!callbackData.startsWith("inbox:page:")) {
            return;
        }

        int page = Integer.parseInt(callbackData.substring(11));
        Slice<InboxItem> activeInbox = null;

        try {
            activeInbox =
                inboxService.getActiveInbox(
                    tgUserId,
                    page,
                    5
                );
        } catch (RuntimeException e) {
            log.error(
                "Failed to get active inbox for Telegram user {}",
                tgUserId,
                e
            );
        }

        messageEditor.edit(
            chatId,
            messageId,
            TelegramMessages.activeInbox(),
            activeInboxItemsKeyboardFactory.main(
                activeInbox,
                page
            )
        );
    }

}
