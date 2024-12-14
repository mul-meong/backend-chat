package com.mulmeong.chat.chatting.dto.out;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import com.mulmeong.chat.chatting.domain.model.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomCreateResponseDto {
    private String roomUuid;
    private List<Participant> participants;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isNewRoom;

    public static ChatRoomCreateResponseDto fromEntity(ChatRoom chatRoom, boolean isNewRoom) {
        return ChatRoomCreateResponseDto.builder()
                .roomUuid(chatRoom.getRoomUuid())
                .participants(chatRoom.getParticipants())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .isNewRoom(isNewRoom)
                .build();
    }
}
