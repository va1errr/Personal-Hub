package com.va1err.personalhub.controller;

import com.va1err.personalhub.api.inbox.InboxController;
import com.va1err.personalhub.inbox.application.InboxService;
import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.inbox.domain.InboxItemStatus;
import com.va1err.personalhub.shared.exception.UserNotFoundException;
import com.va1err.personalhub.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InboxController.class)
class InboxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InboxService inboxService;

    @Test
    void addInboxItem_shouldReturnBadRequestTgUserIdIsNull() throws Exception {
        mockMvc.perform(post("/inbox")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "content": "test"
                }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0].field").value("tgUserId"))
            .andExpect(jsonPath("$.errors[0].message").value("must not be null"));

        verifyNoInteractions(inboxService);
    }

    @Test
    void addInboxItem_shouldReturnNotFoundWhenUserNotFound() throws Exception {
        Long userId = 12345L;

        when(inboxService.addInboxItem(userId, "test"))
            .thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(post("/inbox")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "tgUserId": 12345,
                    "content": "test"
                }
                """))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("User with Telegram ID=12345 not found"))
            .andExpect(jsonPath("$.errors").doesNotHaveJsonPath());

        verify(inboxService).addInboxItem(userId, "test");
    }

    @Test
    void addInboxItem_shouldReturnBadRequestContentIsBlank() throws Exception {
        mockMvc.perform(post("/inbox")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "tgUserId": 12345,
                    "content": "  "
                }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0].field").value("content"))
            .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));

        verifyNoInteractions(inboxService);
    }

    @Test
    void addInboxItem_shouldReturnAddedInboxItem() throws Exception {
        Long userId = 12345L;
        Instant createdAt = Instant.parse("2026-08-18T10:00:00Z");

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getTgUserId()).thenReturn(userId);
        when(user.getCreatedAt()).thenReturn(createdAt);

        InboxItem inboxItem = mock(InboxItem.class);

        when(inboxItem.getId()).thenReturn(1L);
        when(inboxItem.getUser()).thenReturn(user);
        when(inboxItem.getStatus()).thenReturn(InboxItemStatus.ACTIVE);
        when(inboxItem.getContent()).thenReturn("test");
        when(inboxItem.getCreatedAt()).thenReturn(createdAt);

        when(inboxService.addInboxItem(userId, "test")).thenReturn(inboxItem);

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
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.status").value(InboxItemStatus.ACTIVE.toString()))
            .andExpect(jsonPath("$.content").value("test"))
            .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        verify(inboxService).addInboxItem(userId, "test");
    }

}
