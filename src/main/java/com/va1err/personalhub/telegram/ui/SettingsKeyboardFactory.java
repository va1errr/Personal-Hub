package com.va1err.personalhub.telegram.ui;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.callback.SettingsCallbackAction;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@ConditionalOnTelegramEnabled
@Component
public class SettingsKeyboardFactory {

    public InlineKeyboardMarkup main(String currentTimezone) {
        InlineKeyboardButton timezoneButton =
            InlineKeyboardButton.builder()
                .text(currentTimezone == null ?
                    "Timezone" : "Timezone: " + currentTimezone)
                .callbackData(SettingsCallbackAction.TIMEZONE.data())
                .build();

        InlineKeyboardButton healthCheckButton =
            InlineKeyboardButton.builder()
                .text("Health Check")
                .callbackData(SettingsCallbackAction.HEALTH_CHECK.data())
                .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                new InlineKeyboardRow(timezoneButton),
                new InlineKeyboardRow(healthCheckButton)
            ))
            .build();
    }

    public InlineKeyboardMarkup back() {
        InlineKeyboardButton backButton =
            InlineKeyboardButton.builder()
                .text("← Back")
                .callbackData(SettingsCallbackAction.BACK.data())
                .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(backButton))
            .build();
    }

}
