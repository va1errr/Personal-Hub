package com.va1err.personalhub.api.inbox;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddInboxItemRequest(
    @NotNull Long tgUserId,
    @NotBlank String content
) {
}
