package com.mulmeong.chat.chatbot.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Getter
public class ChatBotResponse { //챗봇 응답

    private String memberUuid;
    private String character;
    private String role;
    private String message;
    private String chatRoomUuid;

    //챗봇 응답처리
    public static ChatBotResponse toChatbotResponse(
            JsonNode jsonNode, String memberUuid, String character, String chatRoomUuid) {
        return ChatBotResponse.builder()
                .role(jsonNode.path("choices").get(0).path("message").path("role").asText())
                .message(jsonNode.path("choices").get(0).path("message").path("content").asText())
                .chatRoomUuid(chatRoomUuid)
                .memberUuid(memberUuid)
                .character(character)
                .build();
    }

    public ChatBotHistory toEntity() {
        return ChatBotHistory.builder()
                .memberUuid(memberUuid)
                .character(character)
                .chatRoomUuid(chatRoomUuid)
                .role(role)
                .message(message)
                .createdAt(Instant.now())
                .build();
    }
}
