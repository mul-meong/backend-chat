package com.mulmeong.chat.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chatbot_chatroom")
public class ChatBotChatRoom {
    String id;
    String memberUuid;
    String character;
    String chatRoomUuid;
}
