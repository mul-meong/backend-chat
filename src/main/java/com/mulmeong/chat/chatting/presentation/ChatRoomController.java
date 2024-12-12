package com.mulmeong.chat.chatting.presentation;

import com.mulmeong.chat.chatting.application.ChatRoomService;
import com.mulmeong.chat.chatting.dto.in.ChatRoomCreateRequestDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomCreateResponseDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomDto;
import com.mulmeong.chat.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅방", description = "채팅방 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/v1/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "회원uuid로 채팅방 생성 또는 조회", description = """
            대화 상대방의 회원 uuid를 받아와 1:1 채팅방을 생성하거나, 이미 생성된 채팅방을 반환합니다.
            """)
    @PostMapping("/counterpart/{counterPartUuid}")
    public BaseResponse<ChatRoomCreateResponseDto> getOrCreateChatRoomByNicknames(
            @RequestHeader("Member-Uuid") String memberUuid,
            @PathVariable("counterPartUuid") String targetMemberUuid) { //대화 상대방의 uuid

        return new BaseResponse<>(chatRoomService.getOrCreateChatRoomByNicknames(
                new ChatRoomCreateRequestDto(memberUuid, targetMemberUuid)));
    }

    @Operation(summary = "내 대화 목록 조회", description = """
            내가 속한 채팅방 목록을 조회합니다. 페이지네이션 및 삭제된 채팅방 제외해 조회 `예정`)
            """)
    // 내 대화 목록 조회
    @GetMapping("/my")
    public BaseResponse<List<ChatRoomDto>> getMyChatRoomList(@RequestHeader("Member-Uuid") String memberUuid) {
        return new BaseResponse<>(chatRoomService.getMyChatRoomList(memberUuid));
    }

    @Operation(summary = "채팅방uuid로 채팅방 정보 조회", description = """
            채팅방 uuid를 받아와 해당 채팅방의 정보를 조회합니다.
            """)
    @GetMapping("/{roomUuid}")
    public BaseResponse<ChatRoomDto> getChatRoomInfoByRoomUuid(@PathVariable String roomUuid) {
        return new BaseResponse<>(chatRoomService.getChatRoomByRoomUuid(roomUuid));
    }
}
