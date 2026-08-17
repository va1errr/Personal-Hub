package com.va1err.personalhub.user.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long tgUserId;

    private String tgUsername;

    @CreationTimestamp(source = SourceType.DB)
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected User() {

    }

    private User(Long tgUserId, String tgUsername) {
        this.tgUserId = Objects.requireNonNull(
                tgUserId,
                "Telegram user ID must not be null"
        );

        this.tgUsername = normalizeUsername(tgUsername);
    }

    public Long getId() {
        return id;
    }

    public Long getTgUserId() {
        return tgUserId;
    }

    public String getTgUsername() {
        return tgUsername;
    }

    public static User register(Long tgUserId, String tgUsername) {
        return new User(tgUserId, tgUsername);
    }

    private static String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return username.strip();
    }


}
