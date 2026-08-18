package com.va1err.personalhub.shared.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long tgUserId) {
        super("User with Telegram ID=" + tgUserId + " not found");
    }
}
