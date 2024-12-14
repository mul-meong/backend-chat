package com.mulmeong.chat.chatting.application;

import com.mulmeong.chat.chatting.dto.in.ChatCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRestServiceImpl implements ChatRestService {

    private final MongoTemplate mongoTemplate;

    @Override
    public Void sendChatMessage(ChatCreateDto chatCreateDto) {
        mongoTemplate.save(chatCreateDto.toEntity());
        return null;
    }

}
