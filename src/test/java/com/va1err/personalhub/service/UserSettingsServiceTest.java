package com.va1err.personalhub.service;

import com.va1err.personalhub.shared.exception.DuplicateUserSettingsException;
import com.va1err.personalhub.shared.exception.UserNotFoundException;
import com.va1err.personalhub.user.application.UserSettingsService;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.domain.UserSettings;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import com.va1err.personalhub.user.infrastructure.UserSettingsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {

    @InjectMocks
    private UserSettingsService userSettingsService;

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<UserSettings> userSettingsCaptor;

    @Test
    void initializeUserSettings_shouldRejectUnregisteredUsers() {
        Long id = 11L;
        String timezone = "Europe/Moscow";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
            UserNotFoundException.class,
            () -> userSettingsService.initializeUserSettings(id, timezone)
        );
        verify(userRepository).findById(id);
        verifyNoInteractions(userSettingsRepository);
    }

    @Test
    void initializeUserSettings_shouldRejectDuplicates() {
        Long id = 11L;
        String timezone = "Europe/Moscow";

        User user = mock(User.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userSettingsRepository.existsByUserId(id)).thenReturn(true);

        assertThrows(
            DuplicateUserSettingsException.class,
            () -> userSettingsService.initializeUserSettings(id, timezone)
        );
        verify(userRepository).findById(id);
        verify(userSettingsRepository).existsByUserId(id);
        verifyNoMoreInteractions(userSettingsRepository);
    }

    @Test
    void initializeUserSettings_shouldReturnCreatedUserSettings() {
        Long id = 11L;
        String timezone = "Europe/Moscow";

        User user = mock(User.class);

        when(user.getId()).thenReturn(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userSettingsService.initializeUserSettings(id, timezone);

        verify(userSettingsRepository).save(userSettingsCaptor.capture());

        UserSettings savedUserSettings = userSettingsCaptor.getValue();

        assertEquals(id, savedUserSettings.getUser().getId());
        assertEquals(timezone, savedUserSettings.getTimezone());
    }

}
