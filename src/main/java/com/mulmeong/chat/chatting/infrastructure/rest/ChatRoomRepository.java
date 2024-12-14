package com.mulmeong.chat.chatting.infrastructure.rest;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    Optional<ChatRoom> findByRoomUuid(String roomUuid);

    // 특정 MemberUuid들을 모두 포함하는 채팅방 조회(1:1 채팅방이므로 2개의 MemberUuid가 모두 포함되는 채팅방)
    @Query("{ 'participants.memberUuid': { $all: ?0 } }")
    Optional<ChatRoom> findByParticipantsMemberUuidAll(List<String> memberUuids);

    // 본인이 속한 채팅방 목록 조회(participants 배열의 특정 MemberUuid, 삭제되지 않은 채팅방)
    @Query(value = "{ 'participants': { $elemMatch: { 'memberUuid': ?0, 'deleteStatus': false } } }",
            sort = "{ 'createdAt': -1 }")
    Optional<List<ChatRoom>> findByParticipantsMemberUuidAndDeleteStatusFalseOrderByCreatedAtDesc(String memberUuid);
}
