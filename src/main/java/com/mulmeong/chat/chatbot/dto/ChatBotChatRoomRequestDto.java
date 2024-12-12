package com.mulmeong.chat.chatbot.dto;

import com.mulmeong.chat.chatbot.entity.ChatBotChatRoom;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ChatBotChatRoomRequestDto {

    String memberUuid;
    String character;
    String chatRoomUuid;

    public static ChatBotChatRoomRequestDto toDto(String memberUuid, String character) {
        return ChatBotChatRoomRequestDto.builder()
                .memberUuid(memberUuid)
                .character(character)
                .build();
    }

    public ChatBotChatRoom toEntity() {
        return ChatBotChatRoom.builder()
                .memberUuid(memberUuid)
                .character(character)
                .chatRoomUuid(UUID.randomUUID().toString())
                .build();
    }
}
