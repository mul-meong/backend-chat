package com.mulmeong.chat.chatting.dto.out;

import com.mulmeong.chat.chatting.domain.document.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class ChatDto {
    private String roomUuid;
    private String messageType;
    private String message;
    private String senderId;
    private Instant createdAt;

    public static ChatDto fromEntity(Chat chat) {
        return ChatDto.builder()
                .roomUuid(chat.getRoomUuid())
                .messageType(chat.getMessageType())
                .message(chat.getMessage())
                .senderId(chat.getSenderId())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
