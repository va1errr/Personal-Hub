package com.va1err.personalhub.telegram;

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

@ConditionalOnTelegramEnabled
@Component
public class TelegramBot implements LongPollingUpdateConsumer {

    private static final Logger log =
        LoggerFactory.getLogger(TelegramBot.class);

    private final String botToken;
    private final TelegramBotsLongPollingApplication application;
    private final TelegramMessageRouter messageRouter;

    public TelegramBot(
        @Value("${telegram.bot.token}") String botToken,
        TelegramMessageRouter messageRouter
    ) {
        this.botToken = botToken;

        this.application = new TelegramBotsLongPollingApplication();
        this.messageRouter = messageRouter;
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

        messageRouter.route(update.getMessage());
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
