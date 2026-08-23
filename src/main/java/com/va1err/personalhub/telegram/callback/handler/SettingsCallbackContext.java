package com.va1err.personalhub.telegram.callback.handler;

public record SettingsCallbackContext(
    Long tgUserId,
    Long chatId,
    Integer messageId
) {
}
