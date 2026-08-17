package com.va1err.personalhub.api.user;

import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(
    @NotNull Long tgUserId,
    String tgUsername
) {
}
