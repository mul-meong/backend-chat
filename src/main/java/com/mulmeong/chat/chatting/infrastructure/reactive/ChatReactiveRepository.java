package com.mulmeong.chat.chatting.infrastructure.reactive;

import com.mulmeong.chat.chatting.domain.document.Chat;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatReactiveRepository extends ReactiveMongoRepository<Chat, String> {

    @Query("{ 'roomUuid' : ?0 }")
    Flux<Chat> findByRoomUuid(String roomUuid);
}
