package com.va1err.personalhub.telegram.message;

public final class Messages {

    private Messages() {

    }

    public static String registrationCompleted(String firstName, String lastName) {
        return """
            Welcome to Personal Hub, %s %s 👋

            You're all set. Personal Hub is your space for quickly capturing and organizing information right here in Telegram.

            Send me any message to save it to your inbox.
            """.formatted(firstName, lastName);
    }

    public static String alreadyRegistered(String firstName, String lastName) {
        return """
            Welcome back, %s %s 👋

            Send me any message to save it to your inbox.
            """.formatted(firstName, lastName);
    }

    public static String registrationUnavailable() {
        return """
            Personal Hub is temporarily unavailable.

            Please try in a moment.
            """;
    }

    public static String unknownCommand(String command) {
        return """
            Sorry, I don't recognize command %s.
            """.formatted(command);
    }

}
