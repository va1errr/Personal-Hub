package com.va1err.personalhub.user.application;

import com.va1err.personalhub.shared.exception.DuplicateTgUserIdException;
import com.va1err.personalhub.shared.exception.NullTgUserIdException;
import com.va1err.personalhub.user.domain.User;
import com.va1err.personalhub.user.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(Long tgUserId, String tgUsername) {
        if (tgUserId == null) {
            throw new NullTgUserIdException();
        }

        if (userRepository.existsByTgUserId(tgUserId)) {
            throw new DuplicateTgUserIdException(tgUserId);
        }

        return userRepository.save(
            User.register(tgUserId, tgUsername)
        );
    }

}
