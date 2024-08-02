package com.soumya.chatserver.service;

import com.soumya.chatserver.entity.Chat;
import com.soumya.chatserver.repository.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ChatService {
    private final ChatRoomService chatRoomService;
    private final ChatRepository repository;

    public Chat saveChat(Chat chat){
        var chatId=chatRoomService.getChatRoomId(chat.getSenderId(),chat.getRecipientId(),true)
                .orElseThrow();
        chat.setChatId(chatId);
        repository.save(chat);
        return chat;
    }

    public List<Chat> findChatMessages(String senderId,String recipientId){
        var chatId=chatRoomService.getChatRoomId(senderId,recipientId,false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
}
