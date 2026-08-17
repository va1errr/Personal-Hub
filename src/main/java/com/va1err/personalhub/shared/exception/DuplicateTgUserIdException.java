package com.va1err.personalhub.shared.exception;

public class DuplicateTgUserIdException extends RuntimeException {
    public DuplicateTgUserIdException(Long userId) {
        super("User with Telegram ID=" + userId + " already registered");
    }
}
