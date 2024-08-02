package com.soumya.chatserver.repository;

import com.soumya.chatserver.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Integer> {

    List<Chat> findByChatId(String chatId);
}
