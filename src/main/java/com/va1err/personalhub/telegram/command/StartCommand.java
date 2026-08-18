package com.va1err.personalhub.telegram.command;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.message.MessageDeleter;
import com.va1err.personalhub.telegram.message.MessageSender;
import com.va1err.personalhub.telegram.message.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ConditionalOnTelegramEnabled
@Component
public class StartCommand implements Command {

    private static final Logger log =
        LoggerFactory.getLogger(StartCommand.class);

    private final RestClient client;
    private final MessageSender messageSender;
    private final MessageDeleter messageDeleter;

    public StartCommand(
        RestClient.Builder restClientBuilder,
        MessageSender messageSender,
        MessageDeleter messageDeleter,
        @Value("${api.base-url}") String baseUrl
    ) {
        this.client = restClientBuilder
            .baseUrl(baseUrl)
            .build();

        this.messageSender = messageSender;
        this.messageDeleter = messageDeleter;
    }

    @Override
    public String name() {
        return "/start";
    }

    @Override
    public void execute(Message message) {
        Long tgUserId = message.getFrom().getId();
        String tgUsername = message.getFrom().getUserName();

        RegisterUserRequest request = new RegisterUserRequest(
            tgUserId,
            tgUsername
        );

        String responseText;

        try {
            responseText = client.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((httpRequest, httpResponse) -> {
                    int status = httpResponse.getStatusCode().value();

                    if (status >= 200 && status < 300) {
                        return Messages.registrationCompleted(
                            message.getFrom().getFirstName(),
                            message.getFrom().getLastName()
                        );
                    }

                    if (status == 409) {
                        return Messages.alreadyRegistered(
                            message.getFrom().getFirstName(),
                            message.getFrom().getLastName()
                        );
                    }

                    throw new IllegalStateException("API return HTTP " + status);
                });
        } catch (RuntimeException exception) {
            log.error(
                "Registration API request failed for Telegram user {}",
                tgUserId,
                exception
            );

            responseText = Messages.registrationUnavailable();
        }

        boolean responseSent = messageSender.send(message.getChatId(), responseText);

        if (responseSent) {
            messageDeleter.delete(message.getChatId(), message.getMessageId());
        }
    }

    private record RegisterUserRequest(
        Long tgUserId,
        String tgUsername
    ) {

    }

}
