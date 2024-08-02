package com.soumya.chatserver.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatEndpoint {
  String senderId;
  String receiverId;
}
