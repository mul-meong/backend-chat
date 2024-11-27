package com.mulmeong.chat.chatbot.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatBotServiceImpl implements ChatBotService {

    @Value("${API-KEY.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions").build();
    private final CharacterPrompt characterPrompt;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String createChat(String character, String userMessage) {

        String kind = characterPrompt.nimoPrompt;
        if (character.equals("dori")) {
            kind = characterPrompt.doriPrompt;
        }

        // 메시지 구성
        Map<String, Object> request = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", kind),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        // API 호출
        String response = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
}
