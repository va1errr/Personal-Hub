package com.va1err.personalhub.note.domain;

import com.va1err.personalhub.user.domain.User;
import jakarta.persistence.*;

@Entity
@Table(name = "note_tags")
public class NoteTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String tag;

}
