package com.va1err.personalhub.telegram.callback;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageEditor;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.state.TimezoneInputState;
import com.va1err.personalhub.telegram.ui.SettingsKeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@ConditionalOnTelegramEnabled
@Component
public class CallbackQueryHandler {

    private final CallbackQueryAcknowledger callbackQueryAcknowledger;
    private final MessageEditor messageEditor;
    private final TimezoneInputState timezoneInputState;
    private final HealthCheckCallbackHandler healthCheckCallbackHandler;
    private final SettingsKeyboardFactory settingsKeyboardFactory;

    public CallbackQueryHandler(
        CallbackQueryAcknowledger callbackQueryAcknowledger,
        MessageEditor messageEditor,
        TimezoneInputState timezoneInputState,
        HealthCheckCallbackHandler healthCheckCallbackHandler,
        SettingsKeyboardFactory settingsKeyboardFactory
    ) {
        this.callbackQueryAcknowledger = callbackQueryAcknowledger;
        this.messageEditor = messageEditor;
        this.timezoneInputState = timezoneInputState;
        this.healthCheckCallbackHandler = healthCheckCallbackHandler;
        this.settingsKeyboardFactory = settingsKeyboardFactory;
    }

    public void handle(CallbackQuery callbackQuery) {
        callbackQueryAcknowledger.acknowledge(callbackQuery.getId());

        if (callbackQuery.getMessage() == null) {
            return;
        }

        Long tgUserId = callbackQuery.getFrom().getId();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        SettingsCallbackAction action =
            SettingsCallbackAction.from(callbackQuery.getData()).orElse(null);

        if (action == null) {
            return;
        }

        switch (action) {
            case TIMEZONE -> handleTimezone(tgUserId, chatId, messageId);

            case HEALTH_CHECK -> healthCheckCallbackHandler.handle(
                tgUserId,
                chatId,
                messageId
            );

            case BACK -> handleBack(tgUserId, chatId, messageId);
        }
    }

    private void handleTimezone(Long tgUserId, Long chatId, Integer messageId) {
        boolean messageEdited = messageEditor.edit(
            chatId,
            messageId,
            TelegramMessages.changeTimezone(),
            settingsKeyboardFactory.back()
        );

        if (messageEdited) {
            timezoneInputState.begin(
                tgUserId,
                chatId,
                messageId,
                TimezoneInputState.Operation.UPDATE
            );
        }
    }

    private void handleBack(Long tgUserId, Long chatId, Integer messageId) {
        timezoneInputState.clear(tgUserId);

        messageEditor.edit(
            chatId,
            messageId,
            TelegramMessages.settings(),
            settingsKeyboardFactory.main()
        );
    }

}
