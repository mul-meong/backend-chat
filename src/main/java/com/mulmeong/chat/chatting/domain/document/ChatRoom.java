package com.mulmeong.chat.chatting.domain.document;

import com.mulmeong.chat.chatting.domain.model.Participant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@ToString
@Getter
@Document
@NoArgsConstructor
public class ChatRoom {

    @Id
    private String roomUuid;
    private List<Participant> participants;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    @Builder
    public ChatRoom(String roomUuid, List<Participant> participants, Instant createdAt, Instant updatedAt) {
        this.roomUuid = roomUuid;
        this.participants = participants;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ChatRoom updateParticipantDeleteStatus(String memberUuid) {
        List<Participant> updatedParticipants = this.participants.stream()
                .map(participant -> {
                    if (participant.getMemberUuid().equals(memberUuid)) {
                        return Participant.updateDeleteStatusAndTime(memberUuid);
                    }
                    return participant;
                })
                .toList();

        return ChatRoom.builder()
                .roomUuid(this.roomUuid)
                .participants(updatedParticipants)
                .createdAt(this.createdAt)
                .updatedAt(Instant.now())
                .build();
    }

    public ChatRoom updateParticipantReadTimeStamp(String memberUuid) {
        List<Participant> updatedParticipants = this.participants.stream()
                .map(participant -> {
                    if (participant.getMemberUuid().equals(memberUuid)) {
                        return Participant.updateReadTimeStamp(memberUuid);
                    }
                    return participant;
                })
                .toList();

        return ChatRoom.builder()
                .roomUuid(this.roomUuid)
                .participants(updatedParticipants)
                .createdAt(this.createdAt)
                .updatedAt(Instant.now())
                .build();
    }
}