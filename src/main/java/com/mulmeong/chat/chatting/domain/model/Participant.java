package com.mulmeong.chat.chatting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class Participant {
    private String memberUuid;
    private Boolean deleteStatus;
    private Instant deletedAt;
    private Boolean readAt;

    public static Participant defaultParticipant(String memberUuid) {
        return Participant.builder()
                .memberUuid(memberUuid)
                .deleteStatus(false)
                .deletedAt(null)
                .readAt(false)
                .build();
    }
}
