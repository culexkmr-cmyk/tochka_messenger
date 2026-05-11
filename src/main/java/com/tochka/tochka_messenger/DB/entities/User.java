package com.tochka.tochka_messenger.DB.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Setter@Getter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter@Getter
    private String username;
    @Setter@Getter
    private String password;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RefreshToken> refreshTokens = new HashSet<>();
}