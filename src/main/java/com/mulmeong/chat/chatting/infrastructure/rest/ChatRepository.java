package com.mulmeong.chat.chatting.infrastructure.rest;

import com.mulmeong.chat.chatting.domain.document.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {


    @Query("{ 'roomUuid' : ?0 }")
    List<Chat> findByRoomUuid(String roomUuid);

    Optional<Chat> findFirstByRoomUuidOrderByCreatedAtDesc(String roomUuid);

    List<Chat> findByRoomUuidOrderByCreatedAtDesc(String roomUuid);

    List<Chat> findByRoomUuidAndCreatedAtAfter(String roomUuid, Instant readTimeStamp);
}
