package com.va1err.personalhub.user.infrastructure;

import com.va1err.personalhub.user.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    public boolean existsByUserId(Long userId);

}
