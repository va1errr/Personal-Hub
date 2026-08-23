package com.va1err.personalhub.telegram.callback;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ConditionalOnTelegramEnabled
@Component
public class CallbackQueryAcknowledger {

    private static final Logger log =
        LoggerFactory.getLogger(CallbackQueryAcknowledger.class);

    private final TelegramClient telegramClient;

    public CallbackQueryAcknowledger(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public void acknowledge(String callbackQueryId) {
        AnswerCallbackQuery request = AnswerCallbackQuery.builder()
            .callbackQueryId(callbackQueryId)
            .build();

        try {
            telegramClient.execute(request);
        } catch (TelegramApiException exception) {
            log.error(
                "Failed to acknowledge callback query {}",
                callbackQueryId,
                exception
            );
        }
    }

}
