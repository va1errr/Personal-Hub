package com.va1err.personalhub.shared.exception;

public class DuplicateUserSettingsException extends RuntimeException {
    public DuplicateUserSettingsException(Long userId) {
        super("Settings for user with ID=" + userId + " already initialized");
    }
}
