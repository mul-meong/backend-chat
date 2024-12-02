package com.mulmeong.chat.chatbot.infrastructure;

import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatBotHistoryRepository extends MongoRepository<ChatBotHistory, String> {
    List<ChatBotHistory> findByMemberUuidAndCharacter(String memberUuid, String character);

    boolean existsByMemberUuidAndCharacter(String memberUuid, String character);

    boolean existsByMemberUuid(String memberUuid);
}
