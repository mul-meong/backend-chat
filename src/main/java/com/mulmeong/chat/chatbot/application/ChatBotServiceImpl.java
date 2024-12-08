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

        String kind = selectPrompt(requestDto.getCharacter()); //프롬프트 선택

        chatBotHistoryRepository.save(UserRequest.toUserRequest(requestDto).toEntity());
        List<Map<String, String>> chatHistory = getChatHistory(requestDto); //사용자 메세지를 포홤한 최근 10개의 메세지

        // 시스템 메시지 추가
        chatHistory.add(0, Map.of("role", "system", "content", kind));

        // API 요청 데이터
        Map<String, Object> request = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", chatHistory
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

    private String selectPrompt(String character) {
        if ("dori".equals(character)) {
            return characterPrompt.doriPrompt;
        }
        return characterPrompt.nimoPrompt;
    }

    private List<Map<String, String>> getChatHistory(ChatBotRequestDto requestDto) {
        List<ChatBotHistory> history = chatBotHistoryRepositoryCustom
                .getRecentTenChatBotHistories(requestDto.getMemberUuid(), requestDto.getCharacter());
        history.sort(Comparator.comparing(ChatBotHistory::getCreatedAt));
        return history.stream()
                .map(chat -> Map.of(
                        "role", chat.getRole().equals("user") ? "user" : "assistant",
                        "content", chat.getMessage()))
                .collect(Collectors.toList());
    }

}
