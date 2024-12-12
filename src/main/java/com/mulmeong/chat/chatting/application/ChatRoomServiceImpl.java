package com.mulmeong.chat.chatting.application;

import com.mulmeong.chat.chatting.domain.document.ChatRoom;
import com.mulmeong.chat.chatting.dto.in.ChatRoomCreateRequestDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomCreateResponseDto;
import com.mulmeong.chat.chatting.dto.out.ChatRoomDto;
import com.mulmeong.chat.chatting.infrastructure.rest.ChatRoomRepository;
import com.mulmeong.chat.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mulmeong.chat.common.response.BaseResponseStatus.NO_CHAT_ROOM;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅 참여자 닉네임을 통해 채팅방 정보를 조회합니다.
     * 참여자 A, B가 있는 채팅방이 있는지 확인하고 없으면 새로 생성
     * 1:1 채팅방이므로 참여자 A, B가 있는 채팅방은 1개로 제한합니다.
     * //todo : 두명 다 채팅방을 나가면 채팅방 삭제
     *
     * @param chatRoomCreateRequestDto : 참여자 A, B의 uuid
     * @return ChatRoomCreateResponseDto : 생성된 채팅방 정보
     */
    @Override
    public ChatRoomCreateResponseDto getOrCreateChatRoomByNicknames(ChatRoomCreateRequestDto chatRoomCreateRequestDto) {

        boolean isNewRoom = false;
        ChatRoom chatRoom = chatRoomRepository.findByParticipantsMemberUuidAll(List.of(
                        chatRoomCreateRequestDto.getMemberUuid(),
                        chatRoomCreateRequestDto.getCounterPartUuid()))
                .orElse(null);

        if (chatRoom == null) {
            chatRoom = chatRoomRepository.save(chatRoomCreateRequestDto.toEntity());
            isNewRoom = true;
        }

        return ChatRoomCreateResponseDto.fromEntity(chatRoom, isNewRoom);
    }

    /**
     * 본인이 속한 채팅방 목록 조회
     * participants 배열 특정 MemberUuid
     * todo : 페이징처리, 삭제 여부 확인.
     *
     * @param memberUuid : 본인의 uuid
     * @return 본인이 속한 채팅방 목록 및 정보
     */
    @Override
    public List<ChatRoomDto> getMyChatRoomList(String memberUuid) {

        return  chatRoomRepository.findByParticipantsMemberUuid(memberUuid)
                .orElseGet(List::of).stream()
                .map(ChatRoomDto::fromEntity)
                .toList();
    }

    @Override
    public ChatRoomDto getChatRoomByRoomUuid(String roomUuid) {
        return ChatRoomDto.fromEntity(chatRoomRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new BaseException(NO_CHAT_ROOM)));
    }
}
