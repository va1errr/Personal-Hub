package com.va1err.personalhub.service;

import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.domain.InboxItemStatus;
import com.va1err.personalhub.inbox.infrastructure.InboxItemRepository;
import com.va1err.personalhub.shared.exception.InboxItemNotFoundException;
import com.va1err.personalhub.shared.exception.TgUserNotFoundException;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxServiceTest {

    private static final Long TEST_TG_USER_ID = 12345L;
    private static final Long TEST_ID = 11L;

    @InjectMocks
    private InboxService inboxService;

    @Mock
    private InboxItemRepository inboxItemRepository;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<InboxItem> inboxItemCaptor;

    @Test
    void addInboxItem_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
            TgUserNotFoundException.class,
            () -> inboxService.addInboxItem(TEST_TG_USER_ID, "test")
        );
        verify(userRepository).findByTgUserId(TEST_TG_USER_ID);
        verifyNoInteractions(inboxItemRepository);
    }

    @Test
    void addInboxItem_shouldReturnAddedInboxItem() {
        User user = User.register(TEST_TG_USER_ID, null);

        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.of(user));

        inboxService.addInboxItem(TEST_TG_USER_ID, "test");

        verify(inboxItemRepository).save(inboxItemCaptor.capture());

        InboxItem savedInboxItem = inboxItemCaptor.getValue();

        assertSame(user, savedInboxItem.getUser());
        assertEquals("test", savedInboxItem.getContent());
        assertEquals(InboxItemStatus.ACTIVE, savedInboxItem.getStatus());
    }

    @Test
    void getActiveInbox_shouldConstructCorrectPageRequest() {
        User user = User.register(TEST_TG_USER_ID, null);
        InboxItem inboxItem = InboxItem.add(user, "test");

        Slice<InboxItem> expected = new SliceImpl<>(List.of(inboxItem));

        when(inboxItemRepository.findByUser_tgUserIdAndStatus(
            eq(TEST_TG_USER_ID),
            eq(InboxItemStatus.ACTIVE),
            any(Pageable.class)
            )).thenReturn(expected);

        Slice<InboxItem> actual = inboxService.getActiveInbox(
            TEST_TG_USER_ID,
            2,
            100
        );

        ArgumentCaptor<Pageable> pageableCaptor =
            ArgumentCaptor.forClass(Pageable.class);

        verify(inboxItemRepository).findByUser_tgUserIdAndStatus(
            eq(TEST_TG_USER_ID),
            eq(InboxItemStatus.ACTIVE),
            pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertSame(expected, actual);
        assertEquals(2, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals(Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.desc("id")
        ), pageable.getSort());
    }

    @Test
    void getInboxItem_shouldRejectInvalidId() {
        when(inboxItemRepository.findByIdAndUser_tgUserId(TEST_ID, TEST_TG_USER_ID))
            .thenReturn(Optional.empty());

        assertThrows(
            InboxItemNotFoundException.class,
            () -> inboxService.getInboxItem(TEST_ID, TEST_TG_USER_ID)
        );
        verify(inboxItemRepository).findByIdAndUser_tgUserId(TEST_ID, TEST_TG_USER_ID);
    }

    @Test
    void getInboxItem_shouldReturnInboxItem() {
        User user = mock(User.class);

        when(user.getTgUserId()).thenReturn(TEST_TG_USER_ID);

        InboxItem expected = InboxItem.add(user, "test");

        when(inboxItemRepository.findByIdAndUser_tgUserId(TEST_ID, TEST_TG_USER_ID))
            .thenReturn(Optional.of(expected));

        InboxItem actual = inboxService.getInboxItem(TEST_ID, TEST_TG_USER_ID);

        assertEquals(expected.getUser().getTgUserId(), actual.getUser().getTgUserId());
        assertEquals(expected.getContent(), actual.getContent());
    }

}
