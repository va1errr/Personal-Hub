package com.va1err.personalhub.user.application;

import com.va1err.personalhub.shared.exception.DuplicateUserSettingsException;
import com.va1err.personalhub.shared.exception.TgUserNotFoundException;
import com.va1err.personalhub.shared.exception.UserSettingsNotFoundException;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.domain.UserSettings;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import com.va1err.personalhub.user.infrastructure.UserSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public UserSettingsService(
        UserSettingsRepository userSettingsRepository,
        UserRepository userRepository
    ) {
        this.userSettingsRepository = userSettingsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserSettings initializeUserSettings(Long tgUserId, String timezone) {
        User user = userRepository.findByTgUserId(tgUserId)
            .orElseThrow(() -> new TgUserNotFoundException(tgUserId));

        if (userSettingsRepository.existsByUserId(user.getId())) {
            throw new DuplicateUserSettingsException(user.getId());
        }

        return userSettingsRepository.save(
            UserSettings.add(user, timezone)
        );
    }

    @Transactional
    public UserSettings updateUserSettings(Long tgUserId, String timezone) {
        User user = userRepository.findByTgUserId(tgUserId)
            .orElseThrow(() -> new TgUserNotFoundException(tgUserId));

        UserSettings userSettings = userSettingsRepository.findByUserId(user.getId())
            .orElseThrow(() -> new UserSettingsNotFoundException(user.getId()));

        userSettings.changeTimezone(timezone);

        userSettingsRepository.flush();

        return userSettings;
    }

    @Transactional(readOnly = true)
    public UserSettings getUserSettings(Long tgUserId) {
        User user = userRepository.findByTgUserId(tgUserId)
            .orElseThrow(() -> new TgUserNotFoundException(tgUserId));

        return userSettingsRepository.findByUserId(user.getId())
            .orElseThrow(() -> new UserSettingsNotFoundException(user.getId()));
    }

}
