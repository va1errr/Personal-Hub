package com.va1err.personalhub.user.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String timezone;

    protected UserSettings() {

    }

    private UserSettings(User user, String timezone) {
        this.user = user;
        this.timezone = timezone;
    }

    public User getUser() {
        return user;
    }

    public String getTimezone() {
        return timezone;
    }

    public static UserSettings add(User user, String timezone) {
        return new UserSettings(user, timezone);
    }

}
