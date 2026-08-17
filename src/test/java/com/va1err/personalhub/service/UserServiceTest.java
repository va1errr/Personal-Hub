package com.va1err.personalhub.service;

import com.va1err.personalhub.shared.exception.DuplicateTgUserIdException;
import com.va1err.personalhub.user.application.UserService;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void registerUser_shouldRejectDuplicateTgUserId() {
        Long userId = 12345L;

        when(userRepository.existsByTgUserId(userId)).thenReturn(true);

        assertThrows(
            DuplicateTgUserIdException.class,
            () -> userService.registerUser(userId, "test")
        );
        verify(userRepository).existsByTgUserId(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void registerUser_shouldReturnRegisteredUser() {
        Long userId = 12345L;

        userService.registerUser(userId, "test");

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(userId, savedUser.getTgUserId());
        assertEquals("test", savedUser.getTgUsername());
    }

}
