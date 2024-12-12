package com.mulmeong.chat.chatting.domain.document;

import com.mulmeong.chat.chatting.domain.model.Participant;
import lombok.*;
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
}