package com.va1err.personalhub.user.infrastructure;

import com.va1err.personalhub.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTgUserId(Long tgUserId);

}
