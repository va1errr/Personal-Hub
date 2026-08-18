package com.va1err.personalhub.telegram.message;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ConditionalOnTelegramEnabled
@Configuration
public class TelegramMessageConfiguration {

    @Bean
    public TelegramClient telegramClient(
        @Value("${telegram.bot.token}") String botToken
    ) {
        return new OkHttpTelegramClient(botToken);
    }

}
