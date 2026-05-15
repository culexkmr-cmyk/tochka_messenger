package com.tochka.tochka_messenger.DB.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ManyToMany
    @JoinTable(
            name = "message_media",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private Set<Media> mediaList = new HashSet<>(); // ManyToMany с Media

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "is_edited", nullable = false)
    private Boolean isEdited = false;

    @Column(name = "edited_at")
    private Instant editedAt;

    @Column(name = "reply_to_message_id")
    private Long replyToMessageId;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public void editMessage(String newText) {
        this.text = newText;
        this.isEdited = true;
        this.editedAt = Instant.now();
    }

}