package com.tochka.tochka_messenger.DB.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
@Entity
@Table(name = "refreshTokens")
public class RefreshToken {
    @Setter @Getter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter @Getter
    @Column(unique = true)
    private String tokenHash;

    @Setter @Getter
    private Instant expiresAt;

    @Setter @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // Исправлено: правильное имя FK колонки
    private User user;
}