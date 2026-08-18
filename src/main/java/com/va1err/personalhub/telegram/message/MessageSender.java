package com.va1err.personalhub.telegram.message;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ConditionalOnTelegramEnabled
@Component
public class MessageSender {

    private static final Logger log =
        LoggerFactory.getLogger(MessageSender.class);

    private final TelegramClient telegramClient;

    public MessageSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public boolean send(Long chatId, String text) {
        SendMessage request = SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .build();

        try {
            telegramClient.execute(request);

            return true;
        } catch (TelegramApiException exception) {
            log.error(
                "Failed to send Telegram message to chat {}",
                chatId,
                exception
            );

            return false;
        }
    }

}
