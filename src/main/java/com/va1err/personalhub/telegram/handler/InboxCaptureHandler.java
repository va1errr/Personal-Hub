package com.va1err.personalhub.telegram.handler;

import com.va1err.personalhub.api.inbox.AddInboxItemRequest;
import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageDeleter;
import com.va1err.personalhub.telegram.message.MessageSender;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

@ConditionalOnTelegramEnabled
@Component
public class InboxCaptureHandler implements MessageHandler {

    private static final Logger log =
        LoggerFactory.getLogger(InboxCaptureHandler.class);

    private final RestClient client;
    private final MessageSender messageSender;
    private final MessageDeleter messageDeleter;

    public InboxCaptureHandler(
        @Value("${api.base-url}") String baseUrl,
        RestClient.Builder restClientBuilder,
        MessageSender messageSender,
        MessageDeleter messageDeleter
    ) {
        this.client = restClientBuilder
            .baseUrl(baseUrl)
            .build();

        this.messageSender = messageSender;
        this.messageDeleter = messageDeleter;
    }

    @Override
    public void handle(Message message) {
        Long tgUserId = message.getFrom().getId();
        String content = message.getText();

        AddInboxItemRequest request = new AddInboxItemRequest(tgUserId, content);

        String responseText;

        try {
            responseText = client.post()
                .uri("/inbox")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((httpRequest, httpResponse) -> {
                    int status = httpResponse.getStatusCode().value();

                    if (status >= 200 && status < 300) {
                        return TelegramMessages.inboxItemSaved(content);
                    }

                    if (status == 404) {
                        return TelegramMessages.registrationRequired();
                    }

                    throw new IllegalStateException("API return HTTP " + status);
                });
        } catch (RuntimeException exception) {
            log.error(
                "Inbox capturing API request failed for Telegram user {}",
                tgUserId,
                exception
            );

            responseText = TelegramMessages.systemUnavailable();
        }

        boolean responseSent = messageSender.send(message.getChatId(), responseText);

        if (responseSent) {
            messageDeleter.delete(message.getChatId(), message.getMessageId());
        }
    }

}
