package com.va1err.personalhub.task.domain;

import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "inbox_item_id")
    private InboxItem inboxItem;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.OPEN;

    @CreationTimestamp(source = SourceType.DB)
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant dueAt;

    private Instant completedAt;

}
