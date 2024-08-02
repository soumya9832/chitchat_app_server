package com.soumya.chatserver.repository;

import com.soumya.chatserver.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Integer> {

    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId,String recipientId);
}
