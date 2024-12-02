package com.mulmeong.chat.chatbot.dto;

import com.mulmeong.chat.chatbot.vo.ChatBotRequestVo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatBotRequestDto {
    private String memberUuid;
    private String character;
    private String role;
    private String message;

    public static ChatBotRequestDto toDto(ChatBotRequestVo requestVo, String character, String user) {
        return ChatBotRequestDto.builder()
                .memberUuid(requestVo.getMemberUuid())
                .message(requestVo.getMessage())
                .character(character)
                .role(user)
                .build();
    }

}
