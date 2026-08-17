package com.va1err.personalhub.shared.exception;

public class NullTgUserIdException extends RuntimeException {
    public NullTgUserIdException() {
        super("Telegram user ID must not be null");
    }
}
