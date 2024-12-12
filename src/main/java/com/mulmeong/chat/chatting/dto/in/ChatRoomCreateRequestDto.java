package com.mulmeong.chat.chatting.dto.in;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.mulmeong.chat.chatting.domain.model.Participant.defaultParticipant;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomCreateRequestDto {

    private String memberUuid;
    private String counterPartUuid;

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .roomUuid(UUID.randomUUID().toString())
                .participants(List.of(
                        defaultParticipant(memberUuid),
                        defaultParticipant(counterPartUuid)))
                .createdAt(Instant.now())
                .build();
    }
}
