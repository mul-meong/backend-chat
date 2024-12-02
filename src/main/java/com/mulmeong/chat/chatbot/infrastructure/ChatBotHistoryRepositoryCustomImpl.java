package com.mulmeong.chat.chatbot.infrastructure;

import com.mulmeong.chat.chatbot.dto.ChatBotHistoryResponseDto;
import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import com.mulmeong.chat.chatbot.entity.QChatBotHistory;
import com.mulmeong.chat.common.utils.CursorPage;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatBotHistoryRepositoryCustomImpl implements ChatBotHistoryRepositoryCustom {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private final MongoTemplate mongoTemplate;
    private final QChatBotHistory chatBotHistory = QChatBotHistory.chatBotHistory;

    @Override
    public CursorPage<ChatBotHistory> getChatBotHistories(
            String memberUuid,
            String character,
            String lastId,
            Integer pageSize,
            Integer pageNo) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(chatBotHistory.memberUuid.eq(memberUuid));
        builder.and(chatBotHistory.character.eq(character));

        if (lastId != null) {
            builder.and(chatBotHistory.id.lt(lastId));
        }

        int currentPage = pageNo != null ? pageNo : DEFAULT_PAGE_NUMBER;
        int currentPageSize = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
        int offset = Math.max(0, (currentPage - 1) * currentPageSize);

        SpringDataMongodbQuery<ChatBotHistory> query
                = new SpringDataMongodbQuery<>(mongoTemplate, ChatBotHistory.class);

        query.where(builder)
                .orderBy(chatBotHistory.id.desc())
                .offset(offset)
                .limit(currentPageSize + 1);

        List<ChatBotHistory> chatBotHistories = query.fetch();


        boolean hasNext = chatBotHistories.size() > currentPageSize;
        String nextCursor = null;
        if (hasNext) {
            chatBotHistories = chatBotHistories.subList(0, currentPageSize);  // 실제 페이지 사이즈 만큼 자르기
            nextCursor = chatBotHistories.get(currentPageSize - 1).getId();

        }

        return new CursorPage<>(chatBotHistories, nextCursor, hasNext, pageSize, pageNo);
    }

    @Override
    public List<ChatBotHistory> getRecentTenChatBotHistories(String memberUuid, String character) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(chatBotHistory.memberUuid.eq(memberUuid));
        builder.and(chatBotHistory.character.eq(character));

        SpringDataMongodbQuery<ChatBotHistory> query
                = new SpringDataMongodbQuery<>(mongoTemplate, ChatBotHistory.class);
        
        return query.where(builder)
                .orderBy(chatBotHistory.id.desc())
                .limit(10)
                .fetch();
    }
}
