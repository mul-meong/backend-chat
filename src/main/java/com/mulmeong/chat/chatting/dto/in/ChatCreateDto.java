package com.mulmeong.chat.chatting.dto.in;

import com.mulmeong.chat.chatting.domain.document.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ChatCreateDto {

    private String roomUuid;
    private String memberUuid;
    private String message;

    public static ChatCreateDto toDto(String roomUuid, String memberUuid, String message) {
        return ChatCreateDto.builder()
                .roomUuid(roomUuid)
                .memberUuid(memberUuid)
                .message(message)
                .build();
    }

    public Chat toEntity() {
        return Chat.builder()
                .roomUuid(roomUuid)
                .senderId(memberUuid)
                .message(message)
                .messageType("TEXT")
                .build();
    }
}
