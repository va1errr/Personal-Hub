package com.va1err.personalhub.telegram.message;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ConditionalOnTelegramEnabled
@Component
public class MessageEditor {

    private static final Logger log =
        LoggerFactory.getLogger(MessageEditor.class);

    private final TelegramClient telegramClient;

    public MessageEditor(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public boolean edit(Long chatId, Integer messageId, String text) {
        EditMessageText request = EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(text)
            .parseMode(ParseMode.HTML)
            .build();

        try {
            telegramClient.execute(request);

            return true;
        } catch (TelegramApiException exception) {
            log.error(
                "Failed to edit Telegram message {} in chat {}",
                messageId,
                chatId,
                exception
            );

            return false;
        }
    }

    public boolean edit(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText request = EditMessageText.builder()
            .chatId(chatId)
            .messageId(messageId)
            .text(text)
            .parseMode(ParseMode.HTML)
            .replyMarkup(keyboard)
            .build();

        try {
            telegramClient.execute(request);

            return true;
        } catch (TelegramApiException exception) {
            log.error(
                "Failed to edit Telegram message {} in chat {}",
                messageId,
                chatId,
                exception
            );

            return false;
        }
    }

}
