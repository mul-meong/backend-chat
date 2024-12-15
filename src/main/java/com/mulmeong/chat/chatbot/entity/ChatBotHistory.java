package com.mulmeong.chat.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chatbot_history")
public class ChatBotHistory {
    @Id
    private String id;
    private String chatRoomUuid;
    private String memberUuid;
    private String character;
    private String role;
    private String message;
    @CreatedDate
    private Instant createdAt;
}
