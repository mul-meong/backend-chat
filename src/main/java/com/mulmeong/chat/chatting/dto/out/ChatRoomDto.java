package com.mulmeong.chat.chatting.dto.out;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import com.mulmeong.chat.chatting.domain.model.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomDto {

    private String roomUuid;
    private List<Participant> participants;
    private Instant createdAt;
    private Instant updatedAt;

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .roomUuid(chatRoom.getRoomUuid())
                .participants(chatRoom.getParticipants())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }
}
