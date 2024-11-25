package com.mulmeong.chat.chatbot.presentation;

import com.mulmeong.chat.chatbot.application.OpenAiService;
import com.mulmeong.chat.common.response.BaseResponse;
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

    private final OpenAiService openAiService;

    @GetMapping
    public String chat(@RequestParam String message) {
        return openAiService.chatWithCharacter(message);
    }
}