package com.va1err.personalhub.telegram.handler;

import com.va1err.personalhub.shared.exception.DuplicateUserSettingsException;
import com.va1err.personalhub.shared.exception.TgUserNotFoundException;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageDeleter;
import com.va1err.personalhub.telegram.message.MessageEditor;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.state.TimezoneInputState;
import com.va1err.personalhub.telegram.ui.SettingsKeyboardFactory;
import com.va1err.personalhub.user.application.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.time.ZoneId;

@ConditionalOnTelegramEnabled
@Component
public class TimezoneInputHandler implements MessageHandler {

    private static final Logger log =
        LoggerFactory.getLogger(TimezoneInputHandler.class);

    private final UserSettingsService userSettingsService;
    private final MessageEditor messageEditor;
    private final MessageDeleter messageDeleter;
    private final TimezoneInputState timezoneInputState;
    private final SettingsKeyboardFactory settingsKeyboardFactory;

    public TimezoneInputHandler(
        UserSettingsService userSettingsService,
        MessageEditor messageEditor,
        MessageDeleter messageDeleter,
        TimezoneInputState timezoneInputState,
        SettingsKeyboardFactory settingsKeyboardFactory
    ) {
        this.userSettingsService = userSettingsService;
        this.messageEditor = messageEditor;
        this.messageDeleter = messageDeleter;
        this.timezoneInputState = timezoneInputState;
        this.settingsKeyboardFactory = settingsKeyboardFactory;
    }

    @Override
    public void handle(Message message) {
        Long tgUserId = message.getFrom().getId();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();

        TimezoneInputState.PendingTimezone pending =
            timezoneInputState.find(tgUserId).orElse(null);

        if (pending == null) {
            return;
        }

        if (!message.hasText()) {
            showInvalidTimezone(pending, chatId, messageId, null);
            return;
        }

        String timezone = message.getText().trim();

        if (!isValidTimezone(timezone)) {
            showInvalidTimezone(pending, chatId, messageId, timezone);
            return;
        }

        String responseText = processTimezone(tgUserId, timezone, pending.operation());

        editPromptAndDeleteInput(
            pending,
            chatId,
            messageId,
            responseText
        );
    }

    private String processTimezone(
        Long tgUserId,
        String timezone,
        TimezoneInputState.Operation operation
    ) {
        try {
            String responseText = executeTimezoneOperation(
                tgUserId,
                timezone,
                operation
            );

            timezoneInputState.clear(tgUserId);
            return responseText;
        } catch (TgUserNotFoundException exception) {
            timezoneInputState.clear(tgUserId);
            return TelegramMessages.registrationRequired();
        } catch (DuplicateUserSettingsException exception) {
            timezoneInputState.clear(tgUserId);
            return TelegramMessages.timezoneAlreadyInitialized();
        } catch (RuntimeException exception) {
            log.error(
                "Timezone operation failed for Telegram user {}",
                tgUserId,
                exception
            );

            return TelegramMessages.systemUnavailable();
        }
    }

    private String executeTimezoneOperation(
        Long tgUserId,
        String timezone,
        TimezoneInputState.Operation operation
    ) {
        return switch (operation) {
            case INITIALIZE -> {
                userSettingsService.initializeUserSettings(
                    tgUserId,
                    timezone
                );

                yield TelegramMessages
                    .timezoneSuccessfullyInitialized(timezone);
            }

            case UPDATE -> {
                userSettingsService.updateUserSettings(
                    tgUserId,
                    timezone
                );

                yield TelegramMessages
                    .timezoneSuccessfullyUpdated(timezone);
            }
        };
    }

    private boolean isValidTimezone(String timezone) {
        return timezone.contains("/")
            && ZoneId.getAvailableZoneIds().contains(timezone);
    }

    private void showInvalidTimezone(
        TimezoneInputState.PendingTimezone pending,
        Long chatId,
        Integer messageId,
        String timezone) {
        editPromptAndDeleteInput(
            pending,
            chatId,
            messageId,
            TelegramMessages.invalidTimezone(timezone)
        );
    }

    private void editPromptAndDeleteInput(
        TimezoneInputState.PendingTimezone pending,
        Long inputChatId,
        Integer inputMessageId,
        String responseText
    ) {
        boolean responseSent = messageEditor.edit(
            pending.chatId(),
            pending.messageId(),
            responseText,
            settingsKeyboardFactory.back()
        );

        if (responseSent) {
            messageDeleter.delete(inputChatId, inputMessageId);
        }
    }

}
