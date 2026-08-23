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

    private static final Long TEST_TG_USER_ID = 12345L;
    private static final Long TEST_USER_ID = 11L;
    private static final String TIMEZONE = "Europe/Moscow";

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
        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
            TgUserNotFoundException.class,
            () -> userSettingsService.initializeUserSettings(TEST_TG_USER_ID, TIMEZONE)
        );
        verify(userRepository).findByTgUserId(TEST_TG_USER_ID);
        verifyNoInteractions(userSettingsRepository);
    }

    @Test
    void initializeUserSettings_shouldRejectDuplicates() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(11L);

        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.of(user));
        when(userSettingsRepository.existsByUserId(user.getId())).thenReturn(true);

        assertThrows(
            DuplicateUserSettingsException.class,
            () -> userSettingsService.initializeUserSettings(TEST_TG_USER_ID, TIMEZONE)
        );
        verify(userRepository).findByTgUserId(TEST_TG_USER_ID);
        verify(userSettingsRepository).existsByUserId(user.getId());
        verifyNoMoreInteractions(userSettingsRepository);
    }

    @Test
    void initializeUserSettings_shouldReturnCreatedUserSettings() {
        User user = mock(User.class);

        when(user.getTgUserId()).thenReturn(TEST_TG_USER_ID);

        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.of(user));

        userSettingsService.initializeUserSettings(TEST_TG_USER_ID, TIMEZONE);

        verify(userSettingsRepository).save(userSettingsCaptor.capture());

        UserSettings savedUserSettings = userSettingsCaptor.getValue();

        assertEquals(TEST_TG_USER_ID, savedUserSettings.getUser().getTgUserId());
        assertEquals(TIMEZONE, savedUserSettings.getTimezone());
    }

    @Test
    void updateUserSettings_shouldRejectUnregisteredUsers() {
        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
            TgUserNotFoundException.class,
            () -> userSettingsService.updateUserSettings(TEST_TG_USER_ID, TIMEZONE)
        );
        verify(userRepository).findByTgUserId(TEST_TG_USER_ID);
        verifyNoInteractions(userSettingsRepository);
    }

    @Test
    void updateUserSettings_shouldRejectUninitializedUserSettings() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(TEST_USER_ID);

        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.of(user));
        when(userSettingsRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
            UserSettingsNotFoundException.class,
            () -> userSettingsService.updateUserSettings(TEST_TG_USER_ID, TIMEZONE)
        );
        verify(userRepository).findByTgUserId(TEST_TG_USER_ID);
        verify(userSettingsRepository).findByUserId(TEST_USER_ID);
        verifyNoMoreInteractions(userSettingsRepository);
    }

    @Test
    void updateUserSettings_shouldReturnUpdatedUserSettings() {
        String newTimezone = "Asia/Shanghai";

        User user = mock(User.class);

        when(user.getTgUserId()).thenReturn(TEST_TG_USER_ID);

        UserSettings expected = UserSettings.add(user, TIMEZONE);

        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.of(user));
        when(userSettingsRepository.findByUserId(user.getId())).thenReturn(Optional.of(expected));

        UserSettings actual = userSettingsService.updateUserSettings(TEST_TG_USER_ID, newTimezone);

        assertSame(expected, actual);
        assertEquals(TEST_TG_USER_ID, actual.getUser().getTgUserId());
        assertEquals(newTimezone, actual.getTimezone());
    }

    @Test
    void getUserSettings_shouldRejectUnregisteredUsers() {
        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
            TgUserNotFoundException.class,
            () -> userSettingsService.getUserSettings(TEST_TG_USER_ID)
        );
        verify(userRepository).findByTgUserId(TEST_TG_USER_ID);
        verifyNoInteractions(userSettingsRepository);
    }

    @Test
    void getUserSettings_shouldRejectUninitializedUserSettings() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(TEST_USER_ID);

        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.of(user));
        when(userSettingsRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThrows(
            UserSettingsNotFoundException.class,
            () -> userSettingsService.getUserSettings(TEST_TG_USER_ID)
        );
        verify(userRepository).findByTgUserId(TEST_TG_USER_ID);
        verify(userSettingsRepository).findByUserId(TEST_USER_ID);
        verifyNoMoreInteractions(userSettingsRepository);
    }

    @Test
    void getUserSettings_shouldReturnUserSettings() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(TEST_USER_ID);

        UserSettings expected = UserSettings.add(user, TIMEZONE);

        when(userRepository.findByTgUserId(TEST_TG_USER_ID)).thenReturn(Optional.of(user));
        when(userSettingsRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(expected));

        UserSettings actual = userSettingsService.getUserSettings(TEST_TG_USER_ID);

        assertEquals(expected.getUser().getId(), actual.getUser().getId());
        assertEquals(expected.getTimezone(), actual.getTimezone());
    }

}
