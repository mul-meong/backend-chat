package com.mulmeong.chat.chatting.application;

import com.mulmeong.chat.chatting.dto.out.ChatDto;
import reactor.core.publisher.Flux;

public interface ChatReactiveService {

    Flux<ChatDto> getChatByRoomUuid(String roomUuid);

    Flux<ChatDto> getNewChatByRoomUuid(String roomUuid);
}
