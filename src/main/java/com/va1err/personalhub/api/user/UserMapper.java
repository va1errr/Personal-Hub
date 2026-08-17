package com.va1err.personalhub.api.user;

import com.va1err.personalhub.user.domain.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getTgUserId(),
            user.getTgUsername(),
            user.getCreatedAt()
        );
    }

}
