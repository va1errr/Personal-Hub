package com.va1err.personalhub.integration;

import com.va1err.personalhub.config.PostgresTestContainerConfig;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.domain.UserSettings;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import com.va1err.personalhub.user.infrastructure.UserSettingsRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(PostgresTestContainerConfig.class)
class UserSettingsInitializeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void initializeUserSettings_shouldInitializeUserSettings() throws Exception {
        Long userId = 12345L;
        String timezone = "Europe/Moscow";

        User savedUser = userRepository.saveAndFlush(
            User.register(userId, null)
        );

        mockMvc.perform(post("/users/" + savedUser.getId() + "/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "timezone": "Europe/Moscow"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.userId").value(savedUser.getId()))
            .andExpect(jsonPath("$.timezone").value(timezone));

        entityManager.flush();
        entityManager.clear();

        List<UserSettings> result = userSettingsRepository.findAll();

        assertEquals(1, result.size());
        assertEquals(savedUser.getId(), result.getFirst().getUser().getId());
        assertEquals(timezone, result.getFirst().getTimezone());
    }

}
