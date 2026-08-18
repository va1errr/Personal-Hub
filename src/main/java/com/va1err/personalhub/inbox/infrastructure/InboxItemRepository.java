package com.va1err.personalhub.inbox.infrastructure;

import com.va1err.personalhub.inbox.domain.InboxItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxItemRepository extends JpaRepository<InboxItem, Long> {
}
