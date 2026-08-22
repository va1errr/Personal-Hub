package com.va1err.personalhub.telegram.handler;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageEditor;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@ConditionalOnTelegramEnabled
@Component
public class CallbackQueryHandler {

    private static final Logger log =
        LoggerFactory.getLogger(CallbackQueryHandler.class);

    private final RestClient client;
    private final TelegramClient telegramClient;
    private final MessageEditor messageEditor;

    public CallbackQueryHandler(
        @Value("${api.base-url}") String baseUrl,
        RestClient.Builder restClientBuilder,
        TelegramClient telegramClient,
        MessageEditor messageEditor
    ) {
        this.client = restClientBuilder
            .baseUrl(baseUrl)
            .build();

        this.telegramClient = telegramClient;
        this.messageEditor = messageEditor;
    }

    public void handle(CallbackQuery callbackQuery) {
        acknowledge(callbackQuery);

        if (callbackQuery.getMessage() == null) {
            return;
        }

        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String action = callbackQuery.getData();

        switch (action) {
            case "settings:timezone" -> messageEditor.edit(
                chatId,
                messageId,
                TelegramMessages.changeTimezone(),
                backKeyboard()
            );

            case "settings:healthCheck" ->
                messageEditor.edit(
                    chatId,
                    messageId,
                    requestHealthCheck(callbackQuery.getFrom().getId()),
                    backKeyboard()
                );

            case "settings:back" -> messageEditor.edit(
                chatId,
                messageId,
                TelegramMessages.settings(),
                settingsKeyboard()
            );
        }
    }

    private String requestHealthCheck(Long tgUserId) {
        String responseText;

        try {
            responseText = client.get()
                .uri("/actuator/health")
                .exchange((httpRequest, httpResponse) -> {
                    int status = httpResponse.getStatusCode().value();

                    if (status >= 200 && status < 300) {
                        HealthResponse result = httpResponse.bodyTo(HealthResponse.class);

                        if (result != null && result.status.equals("UP")) {
                            return TelegramMessages.healthy();
                        }
                    }

                    return TelegramMessages.unhealthy();
                });
        } catch (RuntimeException exception) {
            log.error(
                "Health API request failed for Telegram user {}",
                tgUserId,
                exception
            );

            responseText = TelegramMessages.systemUnavailable();
        }

        return responseText;
    }

    private InlineKeyboardMarkup settingsKeyboard() {
        InlineKeyboardButton timezoneButton = InlineKeyboardButton.builder()
            .text("Timezone")
            .callbackData("settings:timezone")
            .build();

        InlineKeyboardButton healthCheckButton = InlineKeyboardButton.builder()
            .text("Health Check")
            .callbackData("settings:healthCheck")
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                new InlineKeyboardRow(timezoneButton),
                new InlineKeyboardRow(healthCheckButton)
            ))
            .build();
    }

    private InlineKeyboardMarkup backKeyboard() {
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
            .text("← Back")
            .callbackData("settings:back")
            .build();

        return InlineKeyboardMarkup.builder()
            .keyboardRow(new InlineKeyboardRow(backButton))
            .build();
    }

    private void acknowledge(CallbackQuery callbackQuery) {
        AnswerCallbackQuery request = AnswerCallbackQuery.builder()
            .callbackQueryId(callbackQuery.getId())
            .build();

        try {
            telegramClient.execute(request);
        } catch (TelegramApiException exception) {
            log.error(
                "Failed to acknowledge callback query {}",
                callbackQuery.getId(),
                exception
            );
        }
    }

    private record HealthResponse(String status) {

    }

}
