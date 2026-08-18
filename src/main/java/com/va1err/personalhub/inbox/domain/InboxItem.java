package com.va1err.personalhub.inbox.domain;

import com.va1err.personalhub.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;

@Entity
@Table(name = "inbox_items")
public class InboxItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InboxItemStatus status = InboxItemStatus.ACTIVE;

    @CreationTimestamp(source = SourceType.DB)
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected InboxItem() {

    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public InboxItemStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private InboxItem(User user, String content) {
        this.user = user;
        this.content = InboxItem.normalizeContent(content);
    }

    public static InboxItem add(User user, String content) {
        return new InboxItem(user, content);
    }

    private static String normalizeContent(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        return content.strip();
    }

}
