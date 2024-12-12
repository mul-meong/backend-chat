package com.mulmeong.chat.chatting.presentation;

import com.mulmeong.chat.chatting.application.ChatReactiveService;
import com.mulmeong.chat.chatting.application.ChatRestService;
import com.mulmeong.chat.chatting.domain.document.Chat;
import com.mulmeong.chat.chatting.dto.in.ChatCreateDto;
import com.mulmeong.chat.chatting.dto.out.ChatDto;
import com.mulmeong.chat.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Tag(name = "채팅", description = "채팅 메시지 관련")
@RequiredArgsConstructor
@RequestMapping("/auth/v1/chatrooms")
@RestController
public class ChatController {

    private final ChatReactiveService chatReactiveService;
    private final ChatRestService chatRestService;

    @Operation(summary = "채팅 메시지 전송", description = """
            채팅방 ID, 메시지를 받아와 채팅 메시지를 전송합니다. Header의 Member-Uuid를 통해 메시지를 보낸 사용자를 확인합니다.
            """)
    @PostMapping("/{roomUuid}")
    public BaseResponse<Mono<Void>> sendChatMessage(
            @PathVariable String roomUuid,
            @RequestHeader("Member-Uuid") String memberUuid,
            @RequestBody String message) {

        chatRestService.sendChatMessage(ChatCreateDto.toDto(
                roomUuid, memberUuid, message));
        return new BaseResponse<>();
    }

    @Operation(summary = "채팅 메시지 조회", description = """
            채팅방 ID를 받아와 해당 채팅방의 이전 채팅 메시지를 조회합니다.
            """)
    @GetMapping(value = "/{roomUuid}/previous", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> getChatByRoomUuid(@PathVariable String roomUuid) {

        return chatReactiveService.getChatByRoomUuid(roomUuid).subscribeOn(Schedulers.boundedElastic());
    }

    @Operation(summary = "채팅 메시지 실시간 조회", description = """
            채팅방 ID를 받아와 실시간으로 채팅을 조회합니다.
            """)
    @GetMapping(value = "/{roomUuid}/new", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> getNewChatByRoomUuid(@PathVariable String roomUuid) {

        return chatReactiveService.getNewChatByRoomUuid(roomUuid);
    }
}
