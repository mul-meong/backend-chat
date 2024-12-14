package com.mulmeong.chat.chatting.application;

import com.mulmeong.chat.chatting.dto.in.ChatCreateDto;

public interface ChatRestService {

    Void sendChatMessage(ChatCreateDto chatCreateDto);


}
