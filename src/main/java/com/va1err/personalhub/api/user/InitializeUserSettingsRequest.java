package com.va1err.personalhub.api.user;

import jakarta.validation.constraints.NotNull;

public record InitializeUserSettingsRequest(
    @NotNull String timezone
) {
}
