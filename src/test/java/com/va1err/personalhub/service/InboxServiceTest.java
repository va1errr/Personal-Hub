package com.va1err.personalhub.service;

import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.domain.InboxItemStatus;
import com.va1err.personalhub.inbox.infrastructure.InboxItemRepository;
import com.va1err.personalhub.shared.exception.UserNotFoundException;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxServiceTest {

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
        Long userId = 12345L;

        when(userRepository.findByTgUserId(userId)).thenReturn(Optional.empty());

        assertThrows(
            UserNotFoundException.class,
            () -> inboxService.addInboxItem(userId, "test")
        );
        verify(userRepository).findByTgUserId(userId);
        verifyNoInteractions(inboxItemRepository);
    }

    @Test
    void addInboxItem_shouldReturnAddedInboxItem() {
        Long userId = 12345L;
        User user = User.register(userId, null);

        when(userRepository.findByTgUserId(userId)).thenReturn(Optional.of(user));

        inboxService.addInboxItem(userId, "test");

        verify(inboxItemRepository).save(inboxItemCaptor.capture());

        InboxItem savedInboxItem = inboxItemCaptor.getValue();

        assertSame(user, savedInboxItem.getUser());
        assertEquals("test", savedInboxItem.getContent());
        assertEquals(InboxItemStatus.ACTIVE, savedInboxItem.getStatus());
    }

}
