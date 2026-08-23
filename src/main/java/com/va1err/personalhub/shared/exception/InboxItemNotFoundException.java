package com.va1err.personalhub.shared.exception;

public class InboxItemNotFoundException extends RuntimeException {
    public InboxItemNotFoundException(Long id) {
        super("Inbox item with ID=" + id + " not found");
    }
}
