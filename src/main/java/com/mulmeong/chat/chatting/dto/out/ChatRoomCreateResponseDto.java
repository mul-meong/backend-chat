package com.mulmeong.chat.chatting.dto.out;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import com.mulmeong.chat.chatting.vo.out.ChatRoomResponseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomCreateResponseDto {

    private String roomUuid;
    private String counterPartUuid;
    private String createdAt;
    private String updatedAt;
    private boolean isNewRoom;

    public ChatRoomResponseVo toVo() {
        return ChatRoomResponseVo.builder()
                .roomUuid(roomUuid)
                .counterPartUuid(counterPartUuid)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static ChatRoomCreateResponseDto fromEntity(ChatRoom chatRoom, boolean isNewRoom) {
        return ChatRoomCreateResponseDto.builder()
                .roomUuid(chatRoom.getRoomUuid())
                .counterPartUuid(chatRoom.getParticipants().get(1).getMemberUuid())
                .createdAt(chatRoom.getCreatedAt().toString())
                .updatedAt(chatRoom.getUpdatedAt().toString())
                .isNewRoom(isNewRoom)
                .build();
    }
}
