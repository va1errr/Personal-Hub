package com.va1err.personalhub.repository;

import com.va1err.personalhub.config.PostgresTestContainerConfig;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
    replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(PostgresTestContainerConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByTgUserId_shouldReturnActualUser() {
        Long expectedUserId = 12345L;
        Long otherUserId = 67890L;

        User expected = userRepository.saveAndFlush(
            User.register(expectedUserId, "expected")
        );
        userRepository.saveAndFlush(
            User.register(otherUserId, "other")
        );

        entityManager.clear();

        User actual = userRepository.findByTgUserId(expectedUserId)
            .orElseThrow();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTgUserId(), actual.getTgUserId());
    }

    @Test
    void findByTgUserId_shouldReturnEmptyWhenNotFound() {
        Long absentUserId = 99999L;

        assertTrue(userRepository.findByTgUserId(absentUserId).isEmpty());
    }

    @Test
    void save_shouldRejectDuplicateTgUserIds() {
        Long userId = 12345L;

        userRepository.saveAndFlush(
            User.register(userId, "ok")
        );

        assertThrows(
            DataIntegrityViolationException.class,
            () -> userRepository.saveAndFlush(
                User.register(userId, "duplicate")
            )
        );
    }

}
