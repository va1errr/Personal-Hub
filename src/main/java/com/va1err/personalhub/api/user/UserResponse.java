package com.va1err.personalhub.api.user;

import java.time.Instant;

public record UserResponse(
    Long id,
    Long tgUserId,
    String tgUsername,
    Instant createdAt
) {
}
