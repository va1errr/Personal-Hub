package com.va1err.personalhub.api.inbox;

import com.va1err.personalhub.inbox.domain.InboxItemStatus;

import java.time.Instant;

public record InboxItemResponse(
    Long id,
    Long userId,
    InboxItemStatus status,
    String content,
    Instant createdAt
) {
}
