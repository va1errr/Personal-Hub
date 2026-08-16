package com.va1err.personalhub.note.domain;

import com.va1err.personalhub.inbox.domain.InboxItem;
import com.va1err.personalhub.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notes")
public class Note {

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
    private String content;

    @CreationTimestamp(source = SourceType.DB)
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToMany
    @JoinTable(
            name = "note_to_tag",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<NoteTag> tags = new HashSet<>();

}
