package com.va1err.personalhub.telegram.callback.handler;

import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageEditor;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.ui.ActiveInboxItemsKeyboardFactory;
import com.va1err.personalhub.user.application.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@ConditionalOnTelegramEnabled
@Component
public class InboxItemCallbackHandler {

    private static final Logger log =
        LoggerFactory.getLogger(InboxItemCallbackHandler.class);

    private final MessageEditor messageEditor;
    private final ActiveInboxItemsKeyboardFactory activeInboxItemsKeyboardFactory;
    private final InboxService inboxService;
    private final UserSettingsService userSettingsService;

    public InboxItemCallbackHandler(
        MessageEditor messageEditor,
        ActiveInboxItemsKeyboardFactory activeInboxItemsKeyboardFactory,
        InboxService inboxService,
        UserSettingsService userSettingsService
    ) {
        this.messageEditor = messageEditor;
        this.activeInboxItemsKeyboardFactory = activeInboxItemsKeyboardFactory;
        this.inboxService = inboxService;
        this.userSettingsService = userSettingsService;
    }

    public void handle(
        Long tgUserId,
        Long chatId,
        Integer messageId,
        String callbackData
    ) {
        String[] parts = callbackData.split(":");

        if (parts.length != 5
            || !parts[0].equals("inbox")
            || !parts[1].equals("item")
            || !parts[3].equals("page")) {

            log.warn("Invalid inbox callback: {}", callbackData);
            return;
        }

        int page = Integer.parseInt(parts[4]);
        Long id = Long.parseLong(parts[2]);

        InboxItem inboxItem;

        try {
            inboxItem = inboxService.getInboxItem(
                id,
                tgUserId
            );
        } catch (RuntimeException e) {
            log.error(
                "Failed to get inbox item {} for Telegram user {}",
                id,
                tgUserId,
                e
            );
            return;
        }

        String timezone;
        try {
            timezone = userSettingsService.getUserSettings(tgUserId).getTimezone();
        } catch (RuntimeException e) {
            log.error(
                "Failed to get timezone for Telegram user {}",
                tgUserId,
                e
            );
            return;
        }

        messageEditor.edit(
            chatId,
            messageId,
            TelegramMessages.inboxItem(
                inboxItem.getContent(),
                inboxItem.getCreatedAt()
                    .atZone(ZoneId.of(timezone))
                    .format(DateTimeFormatter.ofPattern(
                        "d MMM uuuu, HH:mm",
                        Locale.ENGLISH
                    ))
            ),
            activeInboxItemsKeyboardFactory.back(page)
        );
    }

}
