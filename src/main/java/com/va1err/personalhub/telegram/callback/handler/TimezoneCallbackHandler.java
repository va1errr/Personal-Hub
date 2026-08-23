package com.va1err.personalhub.telegram.callback.handler;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.callback.SettingsCallbackAction;
import com.va1err.personalhub.telegram.message.MessageEditor;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.state.TimezoneInputState;
import com.va1err.personalhub.telegram.ui.SettingsKeyboardFactory;
import com.va1err.personalhub.user.application.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ConditionalOnTelegramEnabled
@Component
public class TimezoneCallbackHandler implements SettingsCallbackHandler {

    private static final Logger log =
        LoggerFactory.getLogger(TimezoneCallbackHandler.class);

    private final MessageEditor messageEditor;
    private final TimezoneInputState timezoneInputState;
    private final SettingsKeyboardFactory settingsKeyboardFactory;
    private final UserSettingsService userSettingsService;

    public TimezoneCallbackHandler(
        MessageEditor messageEditor,
        TimezoneInputState timezoneInputState,
        SettingsKeyboardFactory settingsKeyboardFactory,
        UserSettingsService userSettingsService
    ) {
        this.messageEditor = messageEditor;
        this.timezoneInputState = timezoneInputState;
        this.settingsKeyboardFactory = settingsKeyboardFactory;
        this.userSettingsService = userSettingsService;
    }

    @Override
    public SettingsCallbackAction action() {
        return SettingsCallbackAction.TIMEZONE;
    }

    @Override
    public void handle(
        SettingsCallbackContext context) {
        String currentTimezone = null;
        try {
            currentTimezone =
                userSettingsService.getUserSettings(context.tgUserId()).getTimezone();
        } catch (RuntimeException e) {
            log.error(
                "Failed to get user settings for Telegram user {}",
                context.tgUserId(),
                e
            );
        }

        boolean messageEdited = messageEditor.edit(
            context.chatId(),
            context.messageId(),
            TelegramMessages.changeTimezone(currentTimezone),
            settingsKeyboardFactory.back()
        );

        if (messageEdited) {
            timezoneInputState.begin(
                context.tgUserId(),
                context.chatId(),
                context.messageId(),
                TimezoneInputState.Operation.UPDATE
            );
        }
    }
}
