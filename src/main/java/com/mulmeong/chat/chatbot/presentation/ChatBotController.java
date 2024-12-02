package com.mulmeong.chat.chatbot.presentation;

import com.mulmeong.chat.chatbot.application.ChatBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/chatrooms/chatbot")
public class ChatBotController {

    private final ChatBotService chatBotService;

    @GetMapping
    @Operation(summary = "캐릭터 챗봇", tags = {"Chatbot Service"})
    public String chat(
            @Parameter(
                    description = "캐릭터 종류",
                    schema = @Schema(allowableValues = {"nimo", "dori"})
            )
            @RequestParam(value = "character") String character,
            @RequestParam String message
    ) {
        return chatBotService.createChat(character, message);

    }
}