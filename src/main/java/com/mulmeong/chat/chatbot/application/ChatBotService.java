package com.mulmeong.chat.chatbot.application;

import com.mulmeong.chat.chatbot.dto.ChatBotRequestDto;
import com.mulmeong.chat.chatbot.dto.ChatBotHistoryResponseDto;
import com.mulmeong.chat.chatbot.dto.ChatBotResponse;
import com.mulmeong.chat.common.utils.CursorPage;

public interface ChatBotService {
    ChatBotResponse createChat(ChatBotRequestDto requestDto);

    void deleteChat(String memberUuid, String character);

    CursorPage<ChatBotHistoryResponseDto> getChatHistoryByPage(
            String memberUuid,
            String character,
            String lastId,
            Integer pageSize,
            Integer pageNo);
}