package com.mulmeong.chat.chatbot.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChatBotResponse { //챗봇 응답

    private String memberUuid;
    private String character;
    private String role;
    private String message;
    private LocalDateTime createdAt;

    //챗봇 응답처리
    public static ChatBotResponse toChatbotResponse(JsonNode jsonNode, String memberUuid, String character) {
        return ChatBotResponse.builder()
                .role(jsonNode.path("choices").get(0).path("message").path("role").asText())
                .message(jsonNode.path("choices").get(0).path("message").path("content").asText())
                .memberUuid(memberUuid)
                .character(character)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ChatBotHistory toEntity() {
        return ChatBotHistory.builder()
                .memberUuid(memberUuid)
                .character(character)
                .role(role)
                .message(message)
                .createdAt(createdAt)
                .build();
    }
}
