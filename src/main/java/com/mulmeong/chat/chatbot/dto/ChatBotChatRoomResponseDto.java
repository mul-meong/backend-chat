package com.mulmeong.chat.chatbot.dto;

import com.mulmeong.chat.chatbot.entity.ChatBotChatRoom;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatBotChatRoomResponseDto {

    String memberUuid;
    String character;
    String chatRoomUuid;

    public static ChatBotChatRoomResponseDto toDto(ChatBotChatRoom chatBotChatRoom) {
        return ChatBotChatRoomResponseDto.builder()
                .memberUuid(chatBotChatRoom.getMemberUuid())
                .character(chatBotChatRoom.getCharacter())
                .chatRoomUuid(chatBotChatRoom.getChatRoomUuid())
                .build();
    }
}
