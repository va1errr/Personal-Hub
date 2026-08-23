package com.va1err.personalhub.telegram.message;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TelegramMessages {

    private TelegramMessages() {
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    private static String displayName(
        String firstName,
        String lastName
    ) {
        return Stream.of(firstName, lastName)
            .filter(name -> name != null && !name.isBlank())
            .map(TelegramMessages::escapeHtml)
            .collect(Collectors.joining(" "));
    }

    public static String registrationCompleted(
        String firstName,
        String lastName
    ) {
        return """
            Welcome to Personal Hub, %s 👋

            Personal Hub is your space for quickly capturing and organizing information right here in Telegram.
            """.formatted(
            displayName(firstName, lastName)
        );
    }

    public static String alreadyRegistered(
        String firstName,
        String lastName
    ) {
        return """
            Welcome back, %s 👋

            Send me any message to save it to your inbox.
            """.formatted(
            displayName(firstName, lastName)
        );
    }

    public static String systemUnavailable() {
        return """
            Personal Hub is temporarily unavailable.

            Please try in a moment.
            """;
    }

    public static String unknownCommand(String command) {
        return """
            Sorry, I don't recognize command %s.
            """.formatted(escapeHtml(command));
    }

    public static String inboxItemSaved(String content) {
        return """
            Saved to your inbox ✅:

            <i>%s</i>
            """.formatted(escapeHtml(content));
    }

    public static String registrationRequired() {
        return """
            Please use /start to use bot.
            """;
    }

    public static String settings() {
        return """
            Choose what you want to configure:
            """;
    }

    public static String initializeTimezone() {
        return """
            Enter timezone to set to in the <i>Region/City</i> format (e.g. <code>Europe/Moscow</code>).
            You can also use a fixed UTC offset, such as <code>Etc/GMT+3</code> or <code>Etc/GMT-4</code>.
            """;
    }

    public static String changeTimezone(String currentZone) {
        if (currentZone == null) {
            return """
                Enter timezone to change to in the <i>Region/City</i> format (e.g. <code>Europe/Moscow</code>).
                You can also use a fixed UTC offset, such as <code>Etc/GMT+3</code> or <code>Etc/GMT-4</code>.
                """;
        }

        return """
            Your current timezone: <code>%s</code>.

            Enter timezone to change to in the <i>Region/City</i> format (e.g. <code>Europe/Moscow</code>).
            You can also use a fixed UTC offset, such as <code>Etc/GMT+3</code> or <code>Etc/GMT-4</code>.
            """.formatted(escapeHtml(currentZone));
    }

    public static String invalidTimezone(String timezone) {
        if (timezone == null) {
            return """
                Invalid timezone.

                Please write in the <i>Region/City</i> format (e.g. <code>Europe/Moscow</code>).
                You can also use a fixed UTC offset, such as <code>Etc/GMT+3</code> or <code>Etc/GMT-4</code>.
                """;
        }

        return """
            Invalid timezone.
            Sorry, I do not recognize <b>%s</b>.

            Please write in the <i>Region/City</i> format (e.g. <code>Europe/Moscow</code>).
            You can also use a fixed UTC offset, such as <code>Etc/GMT+3</code> or <code>Etc/GMT-4</code>.
            """.formatted(escapeHtml(timezone));
    }

    public static String timezoneSuccessfullyInitialized(String timezone) {
        return """
            Successfully set your timezone to <code>%s</code>.

            You're all set. Send me any message to save it to your inbox.
            """.formatted(escapeHtml(timezone));
    }

    public static String timezoneSuccessfullyUpdated(String timezone) {
        return """
            Successfully set your timezone to <code>%s</code>.
            """.formatted(escapeHtml(timezone));
    }

    public static String timezoneAlreadyInitialized() {
        return """
            Your timezone is already set.

            Send me any message to save it to your inbox.
            """;
    }

    public static String healthy() {
        return """
            Personal Hub is <b>UP</b>.
            """;
    }

    public static String unhealthy() {
        return """
            Personal Hub is <b>DOWN</b>.
            """;
    }

    public static String activeInbox() {
        return """
            Here is your active inbox items:
            """;
    }

    public static String inboxItem(String content, String createdAt) {
        return """
            📥 Inbox item

            %s

            Added: %s
            """.formatted(escapeHtml(content), escapeHtml(createdAt));
    }

}
