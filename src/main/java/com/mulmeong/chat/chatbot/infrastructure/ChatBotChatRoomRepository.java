package com.mulmeong.chat.chatbot.infrastructure;

import com.mulmeong.chat.chatbot.entity.ChatBotChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatBotChatRoomRepository extends MongoRepository<ChatBotChatRoom, Long> {
    boolean existsByChatRoomUuid(String chatRoomUuid);

    Optional<ChatBotChatRoom> findByChatRoomUuid(String chatRoomUuid);

    boolean existsByMemberUuidAndCharacter(String memberUuid, String character);

    Optional<ChatBotChatRoom> findByMemberUuidAndCharacter(String memberUuid, String character);

    List<ChatBotChatRoom> findByMemberUuid(String membertUuid);
}
