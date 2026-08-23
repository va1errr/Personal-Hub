package com.va1err.personalhub.service;

import com.va1err.personalhub.shared.exception.DuplicateUserSettingsException;
import com.va1err.personalhub.shared.exception.TgUserNotFoundException;
import com.va1err.personalhub.shared.exception.UserSettingsNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
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
        Long tgUserId = 12345L;
        String timezone = "Europe/Moscow";

        when(userRepository.findByTgUserId(tgUserId)).thenReturn(Optional.empty());

        assertThrows(
            TgUserNotFoundException.class,
            () -> userSettingsService.initializeUserSettings(tgUserId, timezone)
        );
        verify(userRepository).findByTgUserId(tgUserId);
        verifyNoInteractions(userSettingsRepository);
    }

    @Test
    void initializeUserSettings_shouldRejectDuplicates() {
        Long tgUserId = 12345L;
        String timezone = "Europe/Moscow";

        User user = mock(User.class);

        when(user.getId()).thenReturn(11L);

        when(userRepository.findByTgUserId(tgUserId)).thenReturn(Optional.of(user));
        when(userSettingsRepository.existsByUserId(user.getId())).thenReturn(true);

        assertThrows(
            DuplicateUserSettingsException.class,
            () -> userSettingsService.initializeUserSettings(tgUserId, timezone)
        );
        verify(userRepository).findByTgUserId(tgUserId);
        verify(userSettingsRepository).existsByUserId(user.getId());
        verifyNoMoreInteractions(userSettingsRepository);
    }

    @Test
    void initializeUserSettings_shouldReturnCreatedUserSettings() {
        Long tgUserId = 12345L;
        String timezone = "Europe/Moscow";

        User user = mock(User.class);

        when(user.getTgUserId()).thenReturn(tgUserId);

        when(userRepository.findByTgUserId(tgUserId)).thenReturn(Optional.of(user));

        userSettingsService.initializeUserSettings(tgUserId, timezone);

        verify(userSettingsRepository).save(userSettingsCaptor.capture());

        UserSettings savedUserSettings = userSettingsCaptor.getValue();

        assertEquals(tgUserId, savedUserSettings.getUser().getTgUserId());
        assertEquals(timezone, savedUserSettings.getTimezone());
    }

    @Test
    void updateUserSettings_shouldRejectUnregisteredUsers() {
        Long tgUserId = 12345L;
        String timezone = "Europe/Moscow";

        when(userRepository.findByTgUserId(tgUserId)).thenReturn(Optional.empty());

        assertThrows(
            TgUserNotFoundException.class,
            () -> userSettingsService.updateUserSettings(tgUserId, timezone)
        );
        verify(userRepository).findByTgUserId(tgUserId);
        verifyNoInteractions(userSettingsRepository);
    }

    @Test
    void updateUserSettings_shouldRejectUninitializedUserSettings() {
        Long tgUserId = 12345L;
        String timezone = "Europe/Moscow";

        User user = mock(User.class);

        when(user.getId()).thenReturn(11L);

        when(userRepository.findByTgUserId(tgUserId)).thenReturn(Optional.of(user));
        when(userSettingsRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(
            UserSettingsNotFoundException.class,
            () -> userSettingsService.updateUserSettings(tgUserId, timezone)
        );
        verify(userRepository).findByTgUserId(tgUserId);
        verify(userSettingsRepository).findByUserId(user.getId());
        verifyNoMoreInteractions(userSettingsRepository);
    }

    @Test
    void updateUserSettings_shouldReturnUpdatedUserSettings() {
        Long tgUserId = 12345L;
        String timezone = "Europe/Moscow";
        String newTimezone = "Asia/Shanghai";

        User user = mock(User.class);

        when(user.getTgUserId()).thenReturn(tgUserId);

        UserSettings userSettings = UserSettings.add(user, timezone);

        when(userRepository.findByTgUserId(tgUserId)).thenReturn(Optional.of(user));
        when(userSettingsRepository.findByUserId(user.getId())).thenReturn(Optional.of(userSettings));

        UserSettings updatedUserSettings = userSettingsService.updateUserSettings(tgUserId, newTimezone);

        assertSame(userSettings, updatedUserSettings);
        assertEquals(tgUserId, updatedUserSettings.getUser().getTgUserId());
        assertEquals(newTimezone, updatedUserSettings.getTimezone());
    }

}
