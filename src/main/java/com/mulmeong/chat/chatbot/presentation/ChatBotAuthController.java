package com.mulmeong.chat.chatbot.presentation;

import com.mulmeong.chat.chatbot.application.ChatBotService;
import com.mulmeong.chat.chatbot.dto.ChatBotHistoryResponseDto;
import com.mulmeong.chat.chatbot.dto.ChatBotRequestDto;
import com.mulmeong.chat.chatbot.dto.ChatBotResponse;
import com.mulmeong.chat.chatbot.vo.ChatBotHistoryResponseVo;
import com.mulmeong.chat.chatbot.vo.ChatBotRequestVo;
import com.mulmeong.chat.common.response.BaseResponse;
import com.mulmeong.chat.common.utils.CursorPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/auth/v1/chatrooms/chatbot")
public class ChatBotAuthController {

    private final ChatBotService chatBotService;

    @GetMapping
    @Operation(summary = "캐릭터별 챗봇", tags = {"ChatBot Service"})
    public BaseResponse<ChatBotResponse> getChatBotHistoryByPage(
            @RequestHeader("Member-Uuid") String memberUuid,
            @Parameter(
                    description = "캐릭터 종류",
                    schema = @Schema(allowableValues = {"nimo", "dori"})
            )
            @RequestParam(value = "character") String character,
            @RequestParam String message
    ) {
        ChatBotRequestVo requestVo = ChatBotRequestVo.builder()
                .message(message)
                .memberUuid(memberUuid)
                .build();
        ChatBotRequestDto requestDto = ChatBotRequestDto.toDto(requestVo, character, "user");
        System.out.println(requestDto.getMessage());
        ChatBotResponse response = chatBotService.createChat(requestDto);
        return new BaseResponse<>(response);
    }

    @DeleteMapping
    @Operation(summary = "챗봇 기록 삭제", tags = {"ChatBot Service"})
    public BaseResponse<Void> deleteChatBotHistory(
            @RequestHeader("Member-Uuid") String memberUuid,
            @Parameter(
                    description = "캐릭터 종류",
                    schema = @Schema(allowableValues = {"nimo", "dori"})
            )
            @RequestParam(value = "character") String character) {
        chatBotService.deleteChat(memberUuid, character);
        return new BaseResponse<>();
    }

    @GetMapping("/history")
    @Operation(summary = "챗봇 대화 내역 페이지네이션 조회", tags = {"ChatBot Service"})
    public BaseResponse<CursorPage<ChatBotHistoryResponseVo>> getChatHistoryByPage(
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
        CursorPage<ChatBotHistoryResponseDto> cursorPage = chatBotService.getChatHistoryByPage(
                memberUuid, character, lastId, pageSize, pageNo);

        return new BaseResponse<>(
                CursorPage.toCursorPage(cursorPage, cursorPage.getContent().stream()
                        .map(ChatBotHistoryResponseDto::toVo).toList())
        );
    }
}