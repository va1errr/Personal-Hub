package com.va1err.personalhub.shared.exception;

public class UserSettingsNotFoundException extends RuntimeException {
    public UserSettingsNotFoundException(Long id) {
        super("User settings for user with ID=" + id + " not found");
    }
}
