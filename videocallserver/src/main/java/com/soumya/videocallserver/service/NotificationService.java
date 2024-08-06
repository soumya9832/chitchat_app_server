package com.soumya.videocallserver.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    public void sendCallNotification(String token, String title, String body, Map<String, String> data) {
         Notification notification= Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(data)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent call notification : " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
