package com.mulmeong.chat.chatbot.presentation;

import com.mulmeong.chat.chatbot.application.ChatBotService;
import com.mulmeong.chat.chatbot.dto.*;
import com.mulmeong.chat.chatbot.vo.ChatBotHistoryResponseVo;
import com.mulmeong.chat.chatbot.vo.ChatBotRequestVo;
import com.mulmeong.chat.common.response.BaseResponse;
import com.mulmeong.chat.common.utils.CursorPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth/v1/chatrooms/chatbot")
public class ChatBotAuthController {

    private final ChatBotService chatBotService;

    @GetMapping
    @Operation(summary = "캐릭터별 챗봇 대화 생성", tags = {"ChatBot Service"})
    public Mono<ChatBotResponse> getChatBotHistoryByPage(
            @RequestHeader("Member-Uuid") String memberUuid,
            @Parameter(
                    description = "캐릭터 종류",
                    schema = @Schema(allowableValues = {"nimo", "dori"})
            )
            @RequestParam(value = "character") String character,
            @RequestParam String message
    ) {
        log.info("chatbot Controller-> character: {}, message: {}", character, message);
        ChatBotRequestVo requestVo = ChatBotRequestVo.builder()
                .message(message)
                .memberUuid(memberUuid)
                .build();
        ChatBotRequestDto requestDto = ChatBotRequestDto.toDto(requestVo, character, "user");
        Mono<ChatBotResponse> response = chatBotService.createChat(requestDto);
        log.info("chatbot Controller Response -> response: {}", response.toString());
        return response;
    }

    @DeleteMapping
    @Operation(summary = "캐릭터로 챗봇 기록 삭제", tags = {"ChatBot Service"})
    public BaseResponse<Void> deleteChatBotHistoryByCharacter(
            @RequestHeader("Member-Uuid") String memberUuid,
            @Parameter(
                    description = "캐릭터 종류",
                    schema = @Schema(allowableValues = {"nimo", "dori"})
            )
            @RequestParam(value = "character") String character) {
        chatBotService.deleteChat(memberUuid, character);
        return new BaseResponse<>();
    }

    @DeleteMapping("/{chatRoomUuid}")
    @Operation(summary = "채팅방 UUID로 챗봇 기록 삭제", tags = {"ChatBot Service"})
    public BaseResponse<Void> deleteChatBotHistoryByChatRoomUuid(
            @RequestHeader("Member-Uuid") String memberUuid,
            @PathVariable String chatRoomUuid) {
        chatBotService.deleteChatRoom(memberUuid, chatRoomUuid);
        return new BaseResponse<>();
    }


    @GetMapping("/history")
    @Operation(summary = "캐릭터로 챗봇 대화 내역 페이지네이션 조회", tags = {"ChatBot Service"})
    public BaseResponse<CursorPage<ChatBotHistoryResponseVo>> getChatHistoriesByCharacter(
            @RequestHeader("Member-Uuid") String memberUuid,
            @Parameter(
                    description = "캐릭터 종류",
                    schema = @Schema(allowableValues = {"nimo", "dori"})
            )
            @RequestParam(value = "character") String character,
            @RequestParam(value = "nextCursor", required = false) String lastId,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "pageNo", required = false) Integer pageNo
    ) {
        CursorPage<ChatBotHistoryResponseDto> cursorPage = chatBotService.getChatHistoryByCharacter(
                memberUuid, character, lastId, pageSize, pageNo);

        return new BaseResponse<>(
                CursorPage.toCursorPage(cursorPage, cursorPage.getContent().stream()
                        .map(ChatBotHistoryResponseDto::toVo).toList())
        );
    }

    @GetMapping("/history/{chatRoomUuid}")
    @Operation(summary = "채팅방 UUID로 챗봇 대화 내역 페이지네이션 조회", tags = {"ChatBot Service"})
    public BaseResponse<CursorPage<ChatBotHistoryResponseVo>> getChatHistoriesByChatRoomUuid(
            @RequestHeader("Member-Uuid") String memberUuid,
            @PathVariable String chatRoomUuid,
            @RequestParam(value = "nextCursor", required = false) String lastId,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "pageNo", required = false) Integer pageNo
    ) {
        CursorPage<ChatBotHistoryResponseDto> cursorPage = chatBotService.getChatHistoryByChatRoomUuid(
                memberUuid, chatRoomUuid, lastId, pageSize, pageNo);

        return new BaseResponse<>(
                CursorPage.toCursorPage(cursorPage, cursorPage.getContent().stream()
                        .map(ChatBotHistoryResponseDto::toVo).toList())
        );
    }

    @GetMapping("/chat-list")
    @Operation(summary = "사용자의 챗봇 채팅방 조회", tags = {"ChatBot Service"})
    public BaseResponse<List<ChatBotChatRoomResponseDto>> getChatList(
            @RequestHeader("Member-Uuid") String memberUuid) {
        return new BaseResponse<>(chatBotService.getChatBotChatRoom(memberUuid));
    }
}