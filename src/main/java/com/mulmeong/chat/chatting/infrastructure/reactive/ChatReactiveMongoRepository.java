package com.mulmeong.chat.chatting.infrastructure.reactive;

import com.mulmeong.chat.chatting.domain.document.Chat;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatReactiveMongoRepository extends ReactiveMongoRepository<Chat, String> {

    @Query("{ 'roomUuid' : ?0 }") // roomUuid로 첫번째 파라미터 값을 넣어 쿼리를 날림
    Flux<Chat> findByRoomUuid(String roomUuid);
}
