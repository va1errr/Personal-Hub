package com.va1err.personalhub.repository;

import com.va1err.personalhub.config.PostgresTestContainerConfig;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.domain.InboxItemStatus;
import com.va1err.personalhub.inbox.infrastructure.InboxItemRepository;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
    replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(PostgresTestContainerConfig.class)
class InboxItemRepositoryTest {

    @Autowired
    private InboxItemRepository inboxItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByUser_tgUserIdAndStatus_shouldFilterSortAndPaginateItems() {
        User expectedUser = userRepository.saveAndFlush(
            User.register(12345L, "expected"));
        User otherUser = userRepository.saveAndFlush(
            User.register(67890L, "other"));

        InboxItem first = inboxItemRepository.saveAndFlush(
            InboxItem.add(expectedUser, "first"));
        InboxItem second = inboxItemRepository.saveAndFlush(
            InboxItem.add(expectedUser, "second"));
        InboxItem third = inboxItemRepository.saveAndFlush(
            InboxItem.add(expectedUser, "third"));
        InboxItem processed = inboxItemRepository.saveAndFlush(
            InboxItem.add(expectedUser, "processed"));

        inboxItemRepository.saveAndFlush(
            InboxItem.add(otherUser, "other"));

        processed.process();

        entityManager.flush();
        entityManager.clear();

        Pageable firstPageRequest = PageRequest.of(
            0,
            2,
            Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
            )
        );

        Slice<InboxItem> firstPage =
            inboxItemRepository.findByUser_tgUserIdAndStatus(
                expectedUser.getTgUserId(),
                InboxItemStatus.ACTIVE,
                firstPageRequest
            );

        assertEquals(
            List.of(third.getId(), second.getId()),
            firstPage.getContent().stream()
                .map(InboxItem::getId)
                .toList()
        );
        assertTrue(firstPage.hasNext());

        Pageable secondPageRequest = firstPageRequest.next();

        Slice<InboxItem> secondPage =
            inboxItemRepository.findByUser_tgUserIdAndStatus(
                expectedUser.getTgUserId(),
                InboxItemStatus.ACTIVE,
                secondPageRequest
            );

        assertEquals(
            List.of(first.getId()),
            secondPage.getContent().stream()
                .map(InboxItem::getId)
                .toList()
        );
        assertFalse(secondPage.hasNext());
    }

}
