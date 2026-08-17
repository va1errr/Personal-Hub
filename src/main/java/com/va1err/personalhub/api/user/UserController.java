package com.va1err.personalhub.api.user;

import com.va1err.personalhub.user.application.UserService;
import com.va1err.personalhub.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse registerUser(@Valid @RequestBody RegisterUserRequest request) {
        User user = userService.registerUser(
            request.tgUserId(),
            request.tgUsername()
        );

        return UserMapper.toResponse(user);
    }

}
