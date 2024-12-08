package com.mulmeong.chat.chatbot.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatBotHistoryResponseVo {
    private String memberUuid;
    private String character;
    private String role;
    private String message;
    private LocalDateTime createdAt;
}
