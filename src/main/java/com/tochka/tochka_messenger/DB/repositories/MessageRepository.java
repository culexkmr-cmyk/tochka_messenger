package com.tochka.tochka_messenger.DB.repositories;

import com.tochka.tochka_messenger.DB.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
