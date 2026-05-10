package com.tochka.tochka_messenger.DB.repositories;

import com.tochka.tochka_messenger.DB.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
