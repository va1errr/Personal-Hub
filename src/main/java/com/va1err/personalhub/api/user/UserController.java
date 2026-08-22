package com.va1err.personalhub.api.user;

import com.va1err.personalhub.user.application.UserService;
import com.va1err.personalhub.user.application.UserSettingsService;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.domain.UserSettings;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserSettingsService userSettingsService;

    public UserController(
        UserService userService,
        UserSettingsService userSettingsService
    ) {
        this.userService = userService;
        this.userSettingsService = userSettingsService;
    }

    @PostMapping
    public UserResponse registerUser(@Valid @RequestBody RegisterUserRequest request) {
        User user = userService.registerUser(
            request.tgUserId(),
            request.tgUsername()
        );

        return UserMapper.toResponse(user);
    }

    @PostMapping("/{id}/settings")
    public UserSettingsResponse initializeUserSettings(
        @PathVariable Long id,
        @Valid @RequestBody InitializeUserSettingsRequest request
    ) {
        UserSettings userSettings = userSettingsService.initializeUserSettings(
            id,
            request.timezone()
        );

        return new UserSettingsResponse(
            userSettings.getUser().getId(),
            userSettings.getTimezone()
        );
    }

}
