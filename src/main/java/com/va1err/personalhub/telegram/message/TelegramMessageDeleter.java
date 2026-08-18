package com.va1err.personalhub.telegram.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TelegramMessageDeleter {

    private static final Logger log =
        LoggerFactory.getLogger(TelegramMessageDeleter.class);

    private final TelegramClient telegramClient;

    public TelegramMessageDeleter(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public boolean delete(Long chatId, Integer messageId) {
        DeleteMessage request = DeleteMessage.builder()
            .messageId(messageId)
            .chatId(chatId)
            .build();

        try {
            telegramClient.execute(request);

            return true;
        } catch (TelegramApiException exception) {
            log.error(
                "Failed to delete Telegram message {} from chat {}",
                messageId,
                chatId
            );

            return false;
        }
    }

}
