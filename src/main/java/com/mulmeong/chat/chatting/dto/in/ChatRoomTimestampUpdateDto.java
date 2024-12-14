package com.mulmeong.chat.chatting.dto.in;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomTimestampUpdateDto {

    private String roomUuid;
    private String memberUuid;

}
