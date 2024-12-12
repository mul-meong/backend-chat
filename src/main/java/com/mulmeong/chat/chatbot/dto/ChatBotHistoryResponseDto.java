package com.mulmeong.chat.chatbot.dto;

import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import com.mulmeong.chat.chatbot.vo.ChatBotHistoryResponseVo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChatBotHistoryResponseDto {

    private String memberUuid;
    private String character;
    private String role;
    private String message;
    private String chatRoomUuid;
    private LocalDateTime createdAt;


    public static ChatBotHistoryResponseDto toDto(ChatBotHistory chatBotHistory) {
        return ChatBotHistoryResponseDto.builder()
                .role(chatBotHistory.getRole())
                .message(chatBotHistory.getMessage())
                .memberUuid(chatBotHistory.getMemberUuid())
                .createdAt(chatBotHistory.getCreatedAt())
                .character(chatBotHistory.getCharacter())
                .chatRoomUuid(chatBotHistory.getChatRoomUuid())
                .build();
    }

    public ChatBotHistoryResponseVo toVo() {
        return ChatBotHistoryResponseVo.builder()
                .role(role)
                .message(message)
                .memberUuid(memberUuid)
                .createdAt(createdAt)
                .character(character)
                .chatRoomUuid(chatRoomUuid)
                .build();
    }

}
