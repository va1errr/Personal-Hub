package com.va1err.personalhub.telegram.command;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageDeleter;
import com.va1err.personalhub.telegram.message.MessageSender;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@ConditionalOnTelegramEnabled
@Component
public class SettingsCommand implements Command {

    private static final Logger log =
        LoggerFactory.getLogger(SettingsCommand.class);

    private final MessageSender messageSender;
    private final MessageDeleter messageDeleter;

    public SettingsCommand(
        MessageSender messageSender,
        MessageDeleter messageDeleter
    ) {
        this.messageSender = messageSender;
        this.messageDeleter = messageDeleter;
    }

    @Override
    public String name() {
        return "/settings";
    }

    @Override
    public void execute(Message message) {
        InlineKeyboardButton timezoneButton = InlineKeyboardButton.builder()
            .text("Timezone")
            .callbackData("settings:timezone")
            .build();

        InlineKeyboardButton healthCheckButton = InlineKeyboardButton.builder()
            .text("Health Check")
            .callbackData("settings:healthCheck")
            .build();

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                new InlineKeyboardRow(timezoneButton),
                new InlineKeyboardRow(healthCheckButton)
            ))
            .build();

        boolean messageSent = messageSender.send(
            message.getChatId(),
            TelegramMessages.settings(),
            keyboard
        );

        if (messageSent) {
            messageDeleter.delete(message.getChatId(), message.getMessageId());
        }
    }

}
