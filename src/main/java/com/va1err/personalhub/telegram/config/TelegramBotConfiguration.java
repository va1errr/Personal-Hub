package com.va1err.personalhub.telegram.config;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@ConditionalOnTelegramEnabled
@Configuration
public class TelegramBotConfiguration {

    @Bean(destroyMethod = "close")
    public TelegramBotsLongPollingApplication telegramApplication() {
        return new TelegramBotsLongPollingApplication();
    }

}
