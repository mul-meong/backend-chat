package com.mulmeong.chat.chatbot.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatBotRequestVo {

    private String memberUuid;
    private String message;
}
