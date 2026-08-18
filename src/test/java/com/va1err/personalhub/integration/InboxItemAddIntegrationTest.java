package com.va1err.personalhub.integration;

import com.va1err.personalhub.config.PostgresTestContainerConfig;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.domain.InboxItemStatus;
import com.va1err.personalhub.inbox.infrastructure.InboxItemRepository;
import com.va1err.personalhub.telegram.TelegramBot;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
public class InboxItemAddIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InboxItemRepository inboxItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void addInboxItem_shouldAddInboxItem() throws Exception {
        Long userId = 12345L;

        User savedUser = userRepository.saveAndFlush(
            User.register(userId, null)
        );

        mockMvc.perform(post("/inbox")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "tgUserId": 12345,
                        "content": "test"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.userId").value(savedUser.getId()))
            .andExpect(jsonPath("$.status").value(InboxItemStatus.ACTIVE.toString()))
            .andExpect(jsonPath("$.content").value("test"))
            .andExpect(jsonPath("$.createdAt").exists());

        entityManager.flush();
        entityManager.clear();

        List<InboxItem> result = inboxItemRepository.findAll();

        assertEquals(1, result.size());
        assertEquals(userId, result.getFirst().getUser().getTgUserId());
        assertEquals(InboxItemStatus.ACTIVE, result.getFirst().getStatus());
        assertEquals("test", result.getFirst().getContent());
    }

}
