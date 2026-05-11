package com.tochka.tochka_messenger.DB.repositories;

import com.tochka.tochka_messenger.DB.entities.RefreshToken;
import com.tochka.tochka_messenger.DB.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String hash);
    void deleteAllByUser(User user);
    void deleteByTokenHash(String tokenHash);
    Optional<RefreshToken> findByUserAndTokenHash(User user, String tokenHash);
}