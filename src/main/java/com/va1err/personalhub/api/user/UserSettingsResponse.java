package com.va1err.personalhub.api.user;

public record UserSettingsResponse(
    Long userId,
    String timezone
) {
}
