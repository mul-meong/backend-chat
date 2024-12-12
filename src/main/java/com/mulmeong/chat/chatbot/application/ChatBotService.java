package com.mulmeong.chat.chatbot.application;

import com.mulmeong.chat.chatbot.dto.*;
import com.mulmeong.chat.common.utils.CursorPage;

import java.util.List;

public interface ChatBotService {
    ChatBotResponse createChat(ChatBotRequestDto requestDto);

    void deleteChat(String memberUuid, String character);

    void deleteChatRoom(String memberUuid, String chatRoomUuid);

    CursorPage<ChatBotHistoryResponseDto> getChatHistoryByCharacter(
            String memberUuid,
            String character,
            String lastId,
            Integer pageSize,
            Integer pageNo);

    CursorPage<ChatBotHistoryResponseDto> getChatHistoryByChatRoomUuid(
            String memberUuid,
            String chatRoomUuid,
            String lastId,
            Integer pageSize,
            Integer pageNo);

    List<ChatBotChatRoomResponseDto> getChatBotChatRoom(String memberUuid);
}