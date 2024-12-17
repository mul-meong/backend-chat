package com.mulmeong.chat.chatbot.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.mulmeong.chat.chatbot.dto.*;
import com.mulmeong.chat.chatbot.entity.ChatBotChatRoom;
import com.mulmeong.chat.chatbot.entity.ChatBotHistory;
import com.mulmeong.chat.chatbot.infrastructure.ChatBotChatRoomRepository;
import com.mulmeong.chat.chatbot.infrastructure.ChatBotHistoryRepository;
import com.mulmeong.chat.chatbot.infrastructure.ChatBotHistoryRepositoryCustom;
import com.mulmeong.chat.common.exception.BaseException;
import com.mulmeong.chat.common.response.BaseResponseStatus;
import com.mulmeong.chat.common.utils.CursorPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatBotServiceImpl implements ChatBotService {

    @Value("${API-KEY.key}")
    private String openAiKey;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions").build();

    private final CharacterPrompt characterPrompt;
    private final ChatBotHistoryRepository chatBotHistoryRepository;
    private final ChatBotChatRoomRepository chatBotChatRoomRepository;
    private final ChatBotHistoryRepositoryCustom chatBotHistoryRepositoryCustom;

    @Override
    @Transactional
    public Mono<ChatBotResponse> createChat(ChatBotRequestDto requestDto) {
        log.info("chatbot Service-> requestDto: {}", requestDto);

        // 채팅방 찾기
        ChatBotChatRoom chatRoom = findChatRoom(requestDto.getMemberUuid(), requestDto.getCharacter());

        // 프롬프트 선택
        String kind = selectPrompt(requestDto.getCharacter());

        // 사용자가 보낸 메시지와 채팅방 정보를 DB에 저장
        chatBotHistoryRepository.save(UserRequest.toUserRequest(requestDto, chatRoom.getChatRoomUuid()).toEntity());

        // 최근 10개의 메시지 가져오기
        List<Map<String, String>> chatHistory = getChatHistory(requestDto);

        // 시스템 메시지 추가
        chatHistory.add(0, Map.of("role", "system", "content", kind));

        // API 요청 데이터
        Map<String, Object> request = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", chatHistory
        );

        // API 호출 (비동기적으로 처리)
        Mono<ChatBotResponse> chatResponse = webClient.post()
                .header("Authorization", "Bearer " + openAiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class) // Mono<JsonNode>로 반환
                .flatMap(response -> {
                    // API 응답을 기반으로 ChatBotResponse 생성
                    ChatBotResponse chatBot = ChatBotResponse.toChatbotResponse(
                            response,
                            requestDto.getMemberUuid(),
                            requestDto.getCharacter(),
                            chatRoom.getChatRoomUuid());

                    // DB에 저장 후 반환
                    chatBotHistoryRepository.save(chatBot.toEntity()); // chatBot을 Mono로 반환
                    return Mono.just(chatBot);
                });
        log.info("chatbot Service Response-> response: {}", chatResponse);
        return chatResponse;
    }

    @Override
    @Transactional
    public void deleteChat(String memberUuid, String character) {
        if (!chatBotHistoryRepository.existsByMemberUuid(memberUuid)) {
            throw new BaseException(BaseResponseStatus.NO_DELETE_CHAT_HISTORY_AUTHORITY);
        }
        if (!chatBotHistoryRepository.existsByMemberUuidAndCharacter(memberUuid, character)) {
            throw new BaseException(BaseResponseStatus.NO_EXIST_CHAT_HISTORY);
        }

        ChatBotChatRoom chatRoom = findChatRoom(memberUuid, character);
        chatBotHistoryRepository.deleteAll(
                chatBotHistoryRepository.findByMemberUuidAndCharacter(memberUuid, character));

        chatBotChatRoomRepository.delete(chatRoom);
    }

    @Override
    @Transactional
    public void deleteChatRoom(String memberUuid, String chatRoomUuid) {
        if (!chatBotHistoryRepository.existsByMemberUuid(memberUuid)) {
            throw new BaseException(BaseResponseStatus.NO_DELETE_CHAT_HISTORY_AUTHORITY);
        }
        ChatBotChatRoom chatRoom = chatBotChatRoomRepository.findByChatRoomUuid(chatRoomUuid)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_CHAT_ROOM));
        chatBotHistoryRepository.deleteAll(
                chatBotHistoryRepository.findByChatRoomUuid(chatRoomUuid));
        chatBotChatRoomRepository.delete(chatRoom);
    }

    @Override
    public CursorPage<ChatBotHistoryResponseDto> getChatHistoryByCharacter(
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

    @Override
    public CursorPage<ChatBotHistoryResponseDto> getChatHistoryByChatRoomUuid(
            String memberUuid, String chatRoomUuid, String lastId, Integer pageSize, Integer pageNo) {
        CursorPage<ChatBotHistory> cursorPage = chatBotHistoryRepositoryCustom
                .getChatBotHistoriesByChatRoomUuid(memberUuid, chatRoomUuid, lastId, pageSize, pageNo);
        return CursorPage.toCursorPage(cursorPage, cursorPage.getContent().stream()
                .map(ChatBotHistoryResponseDto::toDto).toList());
    }

    @Override
    public List<ChatBotChatRoomResponseDto> getChatBotChatRoom(String memberUuid) {
        return chatBotChatRoomRepository.findByMemberUuid(memberUuid)
                .stream().map(ChatBotChatRoomResponseDto::toDto).toList();
    }

    private ChatBotChatRoom findChatRoom(String memberUuid, String character) {
        if (chatBotChatRoomRepository.existsByMemberUuidAndCharacter(memberUuid, character)) {
            return chatBotChatRoomRepository.findByMemberUuidAndCharacter(memberUuid, character)
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_CHAT_ROOM));
        }
        return chatBotChatRoomRepository.save(ChatBotChatRoomRequestDto.toDto(memberUuid, character)
                .toEntity());
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
