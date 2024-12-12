package com.mulmeong.chat.chatting.application;

import com.mongodb.client.model.changestream.OperationType;
import com.mulmeong.chat.chatting.domain.document.Chat;
import com.mulmeong.chat.chatting.dto.out.ChatDto;
import com.mulmeong.chat.chatting.infrastructure.reactive.ChatReactiveMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatReactiveServiceImpl implements ChatReactiveService {

    private final ChatReactiveMongoRepository chatReactiveRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Flux<ChatDto> getChatByRoomUuid(String roomUuid) {
        return chatReactiveRepository.findByRoomUuid(roomUuid)
                .map(ChatDto::fromEntity);
    }

    @Override
    public Flux<ChatDto> getNewChatByRoomUuid(String roomUuid) {
        log.info("뉴 챗 : " + roomUuid);
        ChangeStreamOptions options = getChangeStreamOptions(roomUuid);

        Flux<Chat> chatFlux = reactiveMongoTemplate.changeStream(// Flux<ChangeStreamEvent<Document>> 반환
                        "chat", // collection 이름
                        options, // ChangeStreamOptions
                        Document.class) // 반환 타입
                .map(ChangeStreamEvent::getBody) // ChangeStreamEvent<Document> -> Document로 변환
                .map(document -> from(document)); // Document -> Chat으로 변환

        log.info("chatFlux: {}", chatFlux);
        return chatFlux.map(ChatDto::fromEntity);
    }

    private static ChangeStreamOptions getChangeStreamOptions(String roomUuid) {
        ChangeStreamOptions options = ChangeStreamOptions.builder() // ChangeStream 옵션 설정
                .filter(Aggregation.newAggregation(// Aggregation을 통해 필터링
                        Aggregation.match(// match를 통해 조건을 설정
                                Criteria.where("operationType").is(OperationType.INSERT.getValue())), // 새로 추가한 문서만 구독
                        Aggregation.match(Criteria.where("fullDocument.roomUuid").is(roomUuid)) // roomUuid가 일치하는 문서만 구독
                ))
                .build();
        log.info("options: {}", options);
        return options;
    }

    private Chat from(Document document) {
        return Chat.builder()
                .roomUuid(document.getString("roomUuid"))
                .messageType(document.getString("messageType"))
                .message(document.getString("message"))
                .senderId(document.getString("senderId"))
                .createdAt(document.getDate("createdAt").toInstant())
                .build();
    }
}
