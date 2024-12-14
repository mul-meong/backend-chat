package com.mulmeong.chat.chatting.presentation;

import com.mulmeong.chat.chatting.application.ChatRoomService;
import com.mulmeong.chat.chatting.dto.in.ChatRoomCreateRequestDto;
import com.mulmeong.chat.chatting.dto.in.ChatRoomTimestampUpdateDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomCreateResponseDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomDto;
import com.mulmeong.chat.chatting.infrastructure.reactive.ChatRoomReactiveRepository;
import com.mulmeong.chat.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "채팅방", description = "채팅방 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/v1/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomReactiveRepository chatRoomReactiveRepository;

    @Operation(summary = "회원uuid로 채팅방 생성 또는 조회", description = """
            대화 상대방의 회원 uuid를 받아와 이미 생성된 채팅방이 있을 경우 기존 채팅방 식별자를 반환하고,
            채팅방이 없을 경우 새로운 채팅방을 생성하고 식별자를 반환합니다.
            상대방의 프로필 페이지와 같이 채팅방ID를 모르는 경우에도 채팅방으로 이동하기 위한 API입니다.
            """)
    @PostMapping("/counterpart/{counterPartUuid}")
    public BaseResponse<ChatRoomCreateResponseDto> getOrCreateChatRoomByNicknames(
            @RequestHeader("Member-Uuid") String memberUuid,
            @PathVariable("counterPartUuid") String targetMemberUuid) { //대화 상대방의 uuid

        return new BaseResponse<>(chatRoomService.getOrCreateChatRoomByNicknames(
                new ChatRoomCreateRequestDto(memberUuid, targetMemberUuid)));
    }

    @Operation(summary = "특정 회원의 모든 채팅방 조회", description = """
            내가 속한 채팅방 목록을 조회합니다. 삭제되지 않고, 최신 메시지가 있는 채팅방으로 정렬되어 반환됩니다.
            채팅방에서의 마지막 메시지가 있는 경우 해당 메시지를 함께 반환합니다. 없는 경우 null로 반환됩니다.
            읽지않은 메시지 수도 함께 반환됩니다.
            """)
    @GetMapping("/my")
    public BaseResponse<List<ChatRoomDto>> getMyChatRoomList(@RequestHeader("Member-Uuid") String memberUuid) {
        return new BaseResponse<>(chatRoomService.getMyChatRoomList(memberUuid));
    }

    @Operation(summary = "채팅방 uuid로 채팅방 정보 조회", description = """
            채팅방 uuid를 받아와 해당 채팅방의 정보를 조회합니다.
            """)
    @GetMapping("/{roomUuid}")
    public BaseResponse<ChatRoomDto> getChatRoomInfoByRoomUuid(@PathVariable String roomUuid) {
        return new BaseResponse<>(chatRoomService.getChatRoomByRoomUuid(roomUuid));
    }

    @Operation(summary = "채팅방 삭제", description = """
            채팅방 ID를 받아와, 해당 채팅방을 삭제합니다.
            상대방이 채팅방을 삭제한 경우 영구 삭제되고, 나만 삭제한 경우 soft delete 처리됩니다. 
            """)
    @DeleteMapping("/{roomUuid}")
    public BaseResponse<Void> deleteChatRoom(@PathVariable String roomUuid,
                                             @RequestHeader("Member-Uuid") String memberUuid) {
        chatRoomService.deleteChatRoom(roomUuid, memberUuid);
        return new BaseResponse<>();
    }

    @Operation(summary = "읽음 시점 변경", description = """
            채팅방 ID 특정 유저의 읽음 시점을 변경합니다. 이 시점을 기준으로 추후 읽지 않은 메시지만 조회하거나, 그 수를 표시할 수 있습니다.
            """)
    @PutMapping("/{roomUuid}/read")
    public Mono<BaseResponse<Void>> updateReadTimestamp(@PathVariable String roomUuid,
                                                        @RequestHeader("Member-Uuid") String memberUuid) {
        return chatRoomService.updateReadTimestamp(new ChatRoomTimestampUpdateDto(roomUuid, memberUuid))
                .thenReturn(new BaseResponse<>());
    }
}
