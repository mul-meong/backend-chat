package com.mulmeong.event.chat;

import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatBotChattingCreateEvent {
    private String memberUuid;
    private String character;
    private String role;
    private String message;
    private LocalDateTime createdAt;

    public static ChatBotChattingCreateEvent toEvent(ChatBotHistory chatBotHistory) {
        return ChatBotChattingCreateEvent.builder()
                .memberUuid(chatBotHistory.getMemberUuid())
                .character(chatBotHistory.getCharacter())
                .role(chatBotHistory.getRole())
                .message(chatBotHistory.getMessage())
                .createdAt(chatBotHistory.getCreatedAt())
                .build();
    }
}
