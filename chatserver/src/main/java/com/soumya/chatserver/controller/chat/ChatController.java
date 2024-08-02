package com.soumya.chatserver.controller.chat;


import com.soumya.chatserver.entity.Chat;
import com.soumya.chatserver.entity.ChatEndpoint;
import com.soumya.chatserver.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
public class ChatController {
    private final ChatService service;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload Chat chat){
        Chat savedMessage=service.saveChat(chat);
//        messagingTemplate.convertAndSendToUser(chat.getRecipientId(),
//                "/queue/messages",
//                new ChatNotification(
//                        savedMessage.getChatId(),
//                        savedMessage.getSenderId(),
//                        savedMessage.getRecipientId(),
//                        savedMessage.getContent()
//                ));
//
//        // Notify the sender as well
//        messagingTemplate.convertAndSendToUser(chat.getSenderId(),
//                "/queue/messages",
//                new ChatNotification(
//                        savedMessage.getChatId(),
//                        savedMessage.getSenderId(),
//                        savedMessage.getRecipientId(),
//                        savedMessage.getContent()
//                ));

        messagingTemplate.convertAndSendToUser(chat.getSenderId(),
                "/queue/messages",
                service.findChatMessages(chat.getSenderId(),chat.getRecipientId()));

        messagingTemplate.convertAndSendToUser(chat.getRecipientId(),
                "/queue/messages",
                service.findChatMessages(chat.getSenderId(),chat.getRecipientId()));
    }

    @MessageMapping("/chat/listen")
    public void getChatMessages(@Payload ChatEndpoint chatEndpoint){
        messagingTemplate.convertAndSendToUser(chatEndpoint.getReceiverId(),
                "/queue/messages",
                service.findChatMessages(chatEndpoint.getSenderId(),chatEndpoint.getReceiverId()));

    }

    @GetMapping("api/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<Chat>> findChatMessages(@PathVariable String senderId, @PathVariable String recipientId) {

        return ResponseEntity.ok(service.findChatMessages(senderId, recipientId));
    }
}
