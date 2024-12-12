package com.mulmeong.chat.chatbot.infrastructure;

import com.mulmeong.chat.chatbot.dto.ChatBotHistoryResponseDto;
import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import com.mulmeong.chat.common.utils.CursorPage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatBotHistoryRepositoryCustom {

    CursorPage<ChatBotHistory> getChatBotHistories(
            String memberUuid,
            String character,
            String lastId,
            Integer pageSize,
            Integer pageNo);

    CursorPage<ChatBotHistory> getChatBotHistoriesByChatRoomUuid(
            String memberUuid,
            String chatRoomUuid,
            String lastId,
            Integer pageSize,
            Integer pageNo);

    List<ChatBotHistory> getRecentTenChatBotHistories(
            String memberUuid,
            String character);
}
