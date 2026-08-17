package com.va1err.personalhub.integration;

import com.va1err.personalhub.config.PostgresTestContainerConfig;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(PostgresTestContainerConfig.class)
public class UserRegisterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void registerUser_shouldRegisterUser() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "tgUserId": 12345,
                        "tgUsername": "test"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.tgUserId").value("12345"))
            .andExpect(jsonPath("$.tgUsername").value("test"))
            .andExpect(jsonPath("$.createdAt").exists());

        entityManager.flush();
        entityManager.clear();

        User result = userRepository.findByTgUserId(12345L)
            .orElseThrow();

        assertNotNull(result.getId());
        assertEquals(12345L, result.getTgUserId());
        assertEquals("test", result.getTgUsername());
        assertNotNull(result.getCreatedAt());
    }

}
