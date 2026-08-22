package com.va1err.personalhub.shared.exception;

public class TgUserNotFoundException extends RuntimeException {
    public TgUserNotFoundException(Long tgUserId) {
        super("User with Telegram ID=" + tgUserId + " not found");
    }
}
