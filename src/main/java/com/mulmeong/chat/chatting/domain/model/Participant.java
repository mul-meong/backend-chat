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
    private Instant readTimeStamp;

    public static Participant defaultParticipant(String memberUuid) {
        return Participant.builder()
                .memberUuid(memberUuid)
                .deleteStatus(false)
                .deletedAt(null)
                .readTimeStamp(Instant.now())
                .build();
    }

    public static Participant updateDeleteStatusAndTime(String memberUuid) {
        return Participant.builder()
                .memberUuid(memberUuid)
                .deleteStatus(true)
                .deletedAt(Instant.now())
                .readTimeStamp(Instant.now())
                .build();
    }

    public static Participant updateReadTimeStamp(String memberUuid) {
        return Participant.builder()
                .memberUuid(memberUuid)
                .deleteStatus(false)
                .deletedAt(null)
                .readTimeStamp(Instant.now())
                .build();
    }

}
