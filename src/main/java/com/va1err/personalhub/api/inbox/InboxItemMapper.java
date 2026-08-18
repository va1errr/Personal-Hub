package com.va1err.personalhub.api.inbox;

import com.va1err.personalhub.inbox.domain.InboxItem;

public class InboxItemMapper {

    public static InboxItemResponse toResponse(InboxItem inboxItem) {
        return new InboxItemResponse(
            inboxItem.getId(),
            inboxItem.getUser().getId(),
            inboxItem.getStatus(),
            inboxItem.getContent(),
            inboxItem.getCreatedAt()
        );
    }

}
