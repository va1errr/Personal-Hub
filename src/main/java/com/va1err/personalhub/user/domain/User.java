package com.va1err.personalhub.user.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;

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

}
