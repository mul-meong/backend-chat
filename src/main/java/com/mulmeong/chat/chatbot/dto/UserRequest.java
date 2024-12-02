package com.mulmeong.chat.chatbot.dto;

import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserRequest { //사용자 메세지

    private String memberUuid;
    private String character;
    private String role;
    private String message;
    private LocalDateTime createdAt;

    public static UserRequest toUserRequest(ChatBotRequestDto dto) {
        return UserRequest.builder()
                .role("user")
                .message(dto.getMessage())
                .memberUuid(dto.getMemberUuid())
                .character(dto.getCharacter())
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
