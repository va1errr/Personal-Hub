package com.va1err.personalhub.user.infrastructure;

import com.va1err.personalhub.user.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    boolean existsByUserId(Long userId);
    Optional<UserSettings> findByUserId(Long userId);

}
