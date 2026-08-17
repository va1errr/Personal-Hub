package com.va1err.personalhub.controller;

import com.va1err.personalhub.api.user.UserController;
import com.va1err.personalhub.shared.exception.DuplicateTgUserIdException;
import com.va1err.personalhub.user.application.UserService;
import com.va1err.personalhub.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void registerUser_shouldReturnBadRequestWhenTgUserIdIsNull() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "tgUserId": null,
                        "tgUsername": "test"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0].field").value("tgUserId"))
            .andExpect(jsonPath("$.errors[0].message").value("must not be null"));

        verifyNoInteractions(userService);
    }

    @Test
    void registerUser_shouldReturnConflictWhenTgUserIdAlreadyExists() throws Exception {
        Long userId = 12345L;

        when(userService.registerUser(userId, "test"))
            .thenThrow(new DuplicateTgUserIdException(userId));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "tgUserId": 12345,
                        "tgUsername": "test"
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.message").value("User with Telegram ID=" + userId + " already registered"))
            .andExpect(jsonPath("$.errors").doesNotHaveJsonPath());

        verify(userService).registerUser(userId, "test");
    }

    @Test
    void registerUser_shouldReturnRegisteredUserWhenTgUsernameIsMissing() throws Exception {
        Long userId = 12345L;
        Instant createdAt = Instant.parse("2026-08-18T10:00:00Z");

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getTgUserId()).thenReturn(userId);
        when(user.getTgUsername()).thenReturn(null);
        when(user.getCreatedAt()).thenReturn(createdAt);

        when(userService.registerUser(userId, null)).thenReturn(user);

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "tgUserId": 12345
                }
                """))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.tgUserId").value(12345))
            .andExpect(jsonPath("$.tgUsername").value(nullValue()))
            .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        verify(userService).registerUser(userId, null);
    }

}
