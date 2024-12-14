package com.mulmeong.chat.chatting.application;

import com.mulmeong.chat.chatting.dto.in.ChatRoomCreateRequestDto;
import com.mulmeong.chat.chatting.dto.in.ChatRoomTimestampUpdateDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomCreateResponseDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatRoomService {

    ChatRoomCreateResponseDto getOrCreateChatRoomByNicknames(ChatRoomCreateRequestDto chatRoomCreateRequestDto);

    List<ChatRoomDto> getMyChatRoomList(String memberUuid);

    ChatRoomDto getChatRoomByRoomUuid(String roomUuid);

    void deleteChatRoom(String roomUuid, String memberUuid);

    Mono<Void> updateReadTimestamp(ChatRoomTimestampUpdateDto chatRoomTimestampUpdateDto);
}
