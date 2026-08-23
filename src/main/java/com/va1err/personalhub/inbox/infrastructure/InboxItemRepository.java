package com.va1err.personalhub.inbox.infrastructure;

import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.domain.InboxItemStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxItemRepository extends JpaRepository<InboxItem, Long> {

    Slice<InboxItem> findByUser_tgUserIdAndStatus(
        Long tgUserId,
        InboxItemStatus status,
        Pageable pageable
    );

}
