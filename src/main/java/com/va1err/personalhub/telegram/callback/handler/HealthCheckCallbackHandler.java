package com.va1err.personalhub.telegram.callback.handler;

import com.va1err.personalhub.telegram.ConditionalOnTelegramEnabled;
import com.va1err.personalhub.telegram.callback.SettingsCallbackAction;
import com.va1err.personalhub.telegram.message.MessageEditor;
import com.va1err.personalhub.telegram.message.TelegramMessages;
import com.va1err.personalhub.telegram.ui.SettingsKeyboardFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.contributor.Status;
import org.springframework.stereotype.Component;

@ConditionalOnTelegramEnabled
@Component
public class HealthCheckCallbackHandler implements SettingsCallbackHandler {

    private static final Logger log =
        LoggerFactory.getLogger(HealthCheckCallbackHandler.class);

    private final HealthEndpoint healthEndpoint;
    private final MessageEditor messageEditor;
    private final SettingsKeyboardFactory settingsKeyboardFactory;

    public HealthCheckCallbackHandler(
        HealthEndpoint healthEndpoint,
        MessageEditor messageEditor,
        SettingsKeyboardFactory settingsKeyboardFactory
    ) {
        this.healthEndpoint = healthEndpoint;
        this.messageEditor = messageEditor;
        this.settingsKeyboardFactory = settingsKeyboardFactory;
    }

    @Override
    public SettingsCallbackAction action() {
        return SettingsCallbackAction.HEALTH_CHECK;
    }

    @Override
    public void handle(
        SettingsCallbackContext context) {
        messageEditor.edit(
            context.chatId(),
            context.messageId(),
            requestHealthCheck(context.tgUserId()),
            settingsKeyboardFactory.back()
        );
    }

    private String requestHealthCheck(Long tgUserId) {
        try {
            Status status = healthEndpoint.health().getStatus();

            return Status.UP.equals(status)
                ? TelegramMessages.healthy()
                : TelegramMessages.unhealthy();
        } catch (RuntimeException exception) {
            log.error(
                "Health check failed for Telegram user {}",
                tgUserId,
                exception
            );

            return TelegramMessages.systemUnavailable();
        }
    }
}
