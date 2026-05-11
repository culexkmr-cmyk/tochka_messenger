package com.tochka.tochka_messenger.DB.repositories;

import com.tochka.tochka_messenger.DB.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);  // Исправлено с getUserByName
}