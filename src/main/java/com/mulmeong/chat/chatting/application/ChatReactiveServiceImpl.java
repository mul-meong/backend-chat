package com.mulmeong.chat.chatting.application;

import com.mongodb.client.model.changestream.OperationType;
import com.mulmeong.chat.chatting.domain.document.Chat;
import com.mulmeong.chat.chatting.dto.out.ChatDto;
import com.mulmeong.chat.chatting.infrastructure.reactive.ChatReactiveRepository;
import com.mulmeong.chat.chatting.infrastructure.reactive.ChatRoomReactiveRepository;
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

    private final ChatReactiveRepository chatReactiveRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ChatRoomReactiveRepository chatRoomReactiveRepository;
    private static final String COLLECTION_NAME = "chat";

    /**
     * 특정 채팅방의 모든 채팅을 가져옴
     * roomUuid로 조회하고, timeStamp 이후의 채팅을 가져와 ChatDto로 변환하여 반환.
     * todo: 페이지네이션.
     *
     * @param roomUuid 채팅방 식별자
     * @return ChatDto의 Flux 반환
     */
    @Override
    public Flux<ChatDto> getChatByRoomUuid(String roomUuid) {

        return chatReactiveRepository.findByRoomUuid(roomUuid)
                .map(ChatDto::fromEntity);
    }

    /**
     * 특정 채팅방의 신규 채팅을 구독
     * ChangeStream 설정을 가져와 신규 채팅을 구독하고, ChatDto로 변환하여 반환.
     *
     * @param roomUuid 채팅방 식별자
     * @return ChatDto의 Flux 반환
     */
    @Override
    public Flux<ChatDto> getNewChatByRoomUuid(String roomUuid) {

        Flux<Chat> chatFlux = reactiveMongoTemplate.changeStream(// Flux<ChangeStreamEvent<Document>> 반환
                        COLLECTION_NAME, getChangeStreamOptions(roomUuid), Document.class) //
                .map(ChangeStreamEvent::getBody) // ChangeStreamEvent<Document> -> Document로 변환
                .map(document -> from(document)); // Document -> Chat으로 변환

        return chatFlux.map(ChatDto::fromEntity);
    }

    // ======================= private =======================

    /**
     * ChangeStreamOptions 설정
     * 실시간 변경사항을 구독하기 위한 옵션 설정.
     *
     * @param roomUuid 채팅방 식별자
     * @return ChangeStreamOptions 객체
     */
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

    /**
     * Document -> Chat 변환.
     *
     * @param document Document 객체
     * @return Chat 객체
     */
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
