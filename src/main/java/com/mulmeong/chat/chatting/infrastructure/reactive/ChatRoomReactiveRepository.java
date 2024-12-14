package com.mulmeong.chat.chatting.infrastructure.reactive;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRoomReactiveRepository extends ReactiveMongoRepository<ChatRoom, String> {

    // uuid로 채팅방 조회
    Mono<ChatRoom> findByRoomUuid(String roomUuid);
}
