package com.mulmeong.chat.chatting.infrastructure.rest;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    Optional<ChatRoom> findByRoomUuid(String roomUuid);

    @Query("{ 'participants.memberUuid': { $all: ?0 } }")
    Optional<ChatRoom> findByParticipantsMemberUuidAll(List<String> memberUuids);

    // 본인이 속한 채팅방 목록 조회(participants 배열의 특정 MemberUuid)
    Optional<List<ChatRoom>> findByParticipantsMemberUuid(String memberUuid);
}
