package com.mulmeong.chat.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chatbot_history")
public class ChatBotHistory {
    @Id
    private String id;
    private String memberUuid;
    private String character;
    private String role;
    private String message;
    private LocalDateTime createdAt;
}
