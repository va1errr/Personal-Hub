package com.va1err.personalhub.telegram;

import com.va1err.personalhub.telegram.command.TelegramCommand;
import com.va1err.personalhub.telegram.message.TelegramMessageDeleter;
import com.va1err.personalhub.telegram.message.TelegramMessageSender;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TelegramBot implements LongPollingUpdateConsumer {

    private static final Logger log =
        LoggerFactory.getLogger(TelegramBot.class);

    private final String botToken;
    private final Map<String, TelegramCommand> commands;
    private final TelegramBotsLongPollingApplication application;
    private final TelegramMessageSender messageSender;
    private final TelegramMessageDeleter messageDeleter;

    public TelegramBot(
        @Value("${telegram.bot.token}") String botToken,
        List<TelegramCommand> commands,
        TelegramMessageSender messageSender,
        TelegramMessageDeleter messageDeleter
    ) {
        this.botToken = botToken;

        this.commands = commands.stream()
            .collect(Collectors.toUnmodifiableMap(
                TelegramCommand::name,
                Function.identity()
            ));

        this.application = new TelegramBotsLongPollingApplication();
        this.messageSender = messageSender;
        this.messageDeleter = messageDeleter;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() throws TelegramApiException {
        application.registerBot(botToken, this);

        log.info("Telegram bot started");
    }

    @Override
    public void consume(List<Update> updates) {
        updates.forEach(this::route);
    }

    private void route(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        var message = update.getMessage();

        if (!message.isUserMessage()) {
            return;
        }

        if (message.getFrom() == null) {
            return;
        }

        if (!message.hasText()) {
            return;
        }

        String text = message.getText().trim();

        if (!text.startsWith("/")) {
            return;
        }

        String commandName = text
            .split("\\s+", 2)[0];

        int botMentionIndex = commandName.indexOf("@");

        if (botMentionIndex >= 0) {
            commandName = commandName.substring(0, botMentionIndex);
        }

        TelegramCommand command = commands.get(commandName);

        if (command == null) {
            boolean responseSent = messageSender.send(
                message.getChatId(),
                TelegramMessages.unknownCommand(commandName)
            );

            if (responseSent) {
                messageDeleter.delete(
                    message.getChatId(),
                    message.getMessageId()
                );

            }

            return;
        }

        command.execute(message);
    }

    @PreDestroy
    public void stop() {
        try {
            application.close();
        } catch (Exception exception) {
            log.error("Failed to stop Telegram bot", exception);
        }
    }

}
