package com.mulmeong.chat.chatbot.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.mulmeong.chat.chatbot.dto.ChatBotRequestDto;
import com.mulmeong.chat.chatbot.dto.ChatBotHistoryResponseDto;
import com.mulmeong.chat.chatbot.dto.ChatBotResponse;
import com.mulmeong.chat.chatbot.dto.UserRequest;
import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import com.mulmeong.chat.chatbot.infrastructure.ChatBotHistoryRepository;
import com.mulmeong.chat.chatbot.infrastructure.ChatBotHistoryRepositoryCustom;
import com.mulmeong.chat.common.exception.BaseException;
import com.mulmeong.chat.common.response.BaseResponseStatus;
import com.mulmeong.chat.common.utils.CursorPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatBotServiceImpl implements ChatBotService {

    @Value("${API-KEY.key}")
    private String apiKey;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions").build();

    private final CharacterPrompt characterPrompt;
    private final ChatBotHistoryRepository chatBotHistoryRepository;
    private final ChatBotHistoryRepositoryCustom chatBotHistoryRepositoryCustom;

    @Override
    public ChatBotResponse createChat(ChatBotRequestDto requestDto) {

        String kind = characterPrompt.nimoPrompt;
        if (requestDto.getCharacter().equals("dori")) {
            kind = characterPrompt.doriPrompt;
        }


        chatBotHistoryRepository.save(UserRequest.toUserRequest(requestDto).toEntity());
        List<ChatBotHistory> chatHistory = chatBotHistoryRepositoryCustom
                .getRecentTenChatBotHistories(requestDto.getMemberUuid(), requestDto.getCharacter());
        chatHistory.sort(Comparator.comparing(ChatBotHistory::getCreatedAt));
        // OpenAI API 요청 메시지 구성
        List<Map<String, String>> storage = chatHistory.stream()
                .map(history -> Map.of(
                        "role", history.getRole().equals("user") ? "user" : "assistant", // OpenAI 호환 role로 변환
                        "content", history.getMessage()
                ))
                .collect(Collectors.toList());

        // 시스템 메시지 추가
        storage.add(0, Map.of("role", "system", "content", kind));

        // API 요청 데이터
        Map<String, Object> request = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", storage
        );
        // API 호출
        JsonNode jsonNode = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        ChatBotResponse chatBot = ChatBotResponse.toChatbotResponse(
                jsonNode, requestDto.getMemberUuid(), requestDto.getCharacter());
        chatBotHistoryRepository.save(chatBot.toEntity());
        return chatBot;
    }

    @Override
    public void deleteChat(String memberUuid, String character) {
        if (!chatBotHistoryRepository.existsByMemberUuid(memberUuid)) {
            throw new BaseException(BaseResponseStatus.NO_DELETE_CHAT_HISTORY_AUTHORITY);
        }
        if (!chatBotHistoryRepository.existsByMemberUuidAndCharacter(memberUuid, character)) {
            throw new BaseException(BaseResponseStatus.NO_EXIST_CHAT_HISTORY);
        }
        chatBotHistoryRepository.deleteAll(
                chatBotHistoryRepository.findByMemberUuidAndCharacter(memberUuid, character));
    }

    @Override
    public CursorPage<ChatBotHistoryResponseDto> getChatHistoryByPage(
            String memberUuid,
            String character,
            String lastId,
            Integer pageSize,
            Integer pageNo) {

        CursorPage<ChatBotHistory> cursorPage = chatBotHistoryRepositoryCustom
                .getChatBotHistories(memberUuid, character, lastId, pageSize, pageNo);
        return CursorPage.toCursorPage(cursorPage, cursorPage.getContent().stream()
                .map(ChatBotHistoryResponseDto::toDto).toList());
    }

}
