package com.va1err.personalhub.user.application;

import com.va1err.personalhub.shared.exception.UserNotFoundException;
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
    public UserSettings initializeUserSettings(Long id, String timezone) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        return userSettingsRepository.save(
            UserSettings.add(user, timezone)
        );
    }

}
