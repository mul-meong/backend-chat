package com.mulmeong.chat.chatting.application;

import com.mulmeong.chat.chatting.domain.document.Chat;
import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import com.mulmeong.chat.chatting.domain.model.Participant;
import com.mulmeong.chat.chatting.dto.in.ChatRoomCreateRequestDto;
import com.mulmeong.chat.chatting.dto.in.ChatRoomTimestampUpdateDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomCreateResponseDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomDto;
import com.mulmeong.chat.chatting.infrastructure.reactive.ChatRoomReactiveRepository;
import com.mulmeong.chat.chatting.infrastructure.rest.ChatRepository;
import com.mulmeong.chat.chatting.infrastructure.rest.ChatRoomRepository;
import com.mulmeong.chat.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static com.mulmeong.chat.common.response.BaseResponseStatus.NO_CHAT_ROOM;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomReactiveRepository chatRoomReactiveRepository;

    /**
     * 채팅 참여자 닉네임을 통해 채팅방 정보를 조회합니다.
     * 참여자 A, B가 있는 채팅방이 있는지 확인하고 없으면 새로 생성
     * 1:1 채팅방이므로 참여자 A, B가 있는 채팅방은 1개로 제한합니다.
     *
     * @param chatRoomCreateRequestDto : 참여자 A, B의 uuid
     * @return ChatRoomCreateResponseDto : 생성된 채팅방 정보
     */
    @Override
    public ChatRoomCreateResponseDto getOrCreateChatRoomByNicknames(ChatRoomCreateRequestDto chatRoomCreateRequestDto) {

        // 1. 채팅방 조회
        boolean isNewRoom = false;
        ChatRoom chatRoom = chatRoomRepository.findByParticipantsMemberUuidAll(List.of(
                        chatRoomCreateRequestDto.getMemberUuid(),
                        chatRoomCreateRequestDto.getCounterPartUuid()))
                .orElse(null);

        // 2. 채팅방이 없는 경우 새로 생성
        if (chatRoom == null) {
            chatRoom = chatRoomRepository.save(chatRoomCreateRequestDto.toEntity());
            isNewRoom = true;
        }

        return ChatRoomCreateResponseDto.fromEntity(chatRoom, isNewRoom);
        // 3. 채팅방 생성 또는 조회 결과 반환
    }

    /**
     * 본인이 속한 채팅방 목록 조회
     * 삭제되지 않은 채팅방 목록을 조회. 삭제 된 경우 조회되지 않기때문에 LastChat은 null
     * 채팅방 목록은 최신 메시지가 있는 순서로 정렬.
     *
     * @param memberUuid : 본인의 uuid
     * @return 본인이 속한 채팅방 목록 및 정보
     */
    @Override
    public List<ChatRoomDto> getMyChatRoomList(String memberUuid) {

        // 채팅방 목록 조회 및 변환
        List<ChatRoomDto> chatRoomDtos
                = chatRoomRepository.findByParticipantsMemberUuidAndDeleteStatusFalseOrderByCreatedAtDesc(memberUuid)
                .orElseGet(List::of).stream()
                .map(chatRoom -> {
                    // 최신순으로 모든 메시지 조회
                    List<Chat> chats = chatRepository.findByRoomUuidOrderByCreatedAtDesc(chatRoom.getRoomUuid());

                    // 최신 메시지 가져오기 (첫 번째 메시지)
                    Chat lastChat = chats.isEmpty() ? null : chats.get(0);

                    // 읽지 않은 메시지 수 계산
                    Participant participant = chatRoom.getParticipants().stream()
                            .filter(p -> p.getMemberUuid().equals(memberUuid))
                            .findFirst()
                            .orElse(null);

                    long unreadCount = 0;
                    if (participant != null && participant.getReadTimeStamp() != null) {
                        unreadCount = chats.stream()
                                .filter(chat -> chat.getCreatedAt().isAfter(participant.getReadTimeStamp()))
                                .count();
                    }

                    // ChatRoomDto 생성
                    return ChatRoomDto.fromEntity(chatRoom, lastChat, unreadCount);
                })
                .toList();

        // 최종 정렬: LastMessageTime과 createdAt 기준으로 최신순 정렬
        return chatRoomDtos.stream()
                .sorted((dto1, dto2) -> {
                    Instant time1 = dto1.getLastMessageTime() != null ? dto1.getLastMessageTime() : dto1.getCreatedAt();
                    Instant time2 = dto2.getLastMessageTime() != null ? dto2.getLastMessageTime() : dto2.getCreatedAt();
                    return time2.compareTo(time1); // 내림차순 정렬
                })
                .toList();
    }

    /**
     * 채팅방 uuid로 채팅방 정보 조회.
     *
     * @param roomUuid : 채팅방 uuid
     * @return 채팅방 정보
     */
    @Override
    public ChatRoomDto getChatRoomByRoomUuid(String roomUuid) {
        return ChatRoomDto.fromEntity(chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new BaseException(NO_CHAT_ROOM)));
    }

    /**
     * 채팅방 삭제
     * 본인이 속한 채팅방의 deleteStatus를 true로 변경
     * 만약 상대방이 이미 삭제한 경우 채팅방을 삭제.
     *
     * @param roomUuid   : 채팅방 uuid
     * @param memberUuid : 본인의 uuid
     */
    @Override
    public void deleteChatRoom(String roomUuid, String memberUuid) {
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new BaseException(NO_CHAT_ROOM));

        List<Participant> participants = chatRoom.getParticipants();

        boolean isCounterPartDeleted = participants.stream()
                .anyMatch(participant -> !participant.getMemberUuid().equals(memberUuid)
                        && participant.getDeleteStatus());

        if (isCounterPartDeleted) {
            // 3. 상대방이 이미 삭제 상태인 경우 채팅방 삭제
            chatRoomRepository.deleteById(roomUuid);
            log.info("삭제 상태인 상대방이 있어 채팅방 삭제. ChatRoom: UUID {}, by uuid: {}", roomUuid, memberUuid);
        } else {
            // 4. 현재 사용자의 삭제 상태 업데이트
            chatRoomRepository.save(chatRoom.updateParticipantDeleteStatus(memberUuid));
            log.info("Participant with UUID {} marked as deleted in ChatRoom with UUID {}", memberUuid, roomUuid);
        }
    }

    /**
     * 채팅방의 참여자 중 본인의 읽음 시점을 업데이트.
     *
     * @param requestDto : 채팅방 uuid, 본인의 uuid
     */
    @Override
    public Mono<Void> updateReadTimestamp(ChatRoomTimestampUpdateDto requestDto) {
        return chatRoomReactiveRepository.findByRoomUuid(requestDto.getRoomUuid())
                .switchIfEmpty(Mono.error(new BaseException(NO_CHAT_ROOM))) // 채팅방이 없는 경우 에러 처리
                .flatMap(chatRoom -> {
                    // 읽음 시점 업데이트 및 저장
                    ChatRoom updatedChatRoom = chatRoom.updateParticipantReadTimeStamp(requestDto.getMemberUuid());
                    return chatRoomReactiveRepository.save(updatedChatRoom);
                })
                .then(); // 결과를 Mono<Void>로 반환
    }
}
