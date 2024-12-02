package com.mulmeong.chat;

import com.mulmeong.event.chat.ChatBotChattingCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${event.chat.pub.topics.chatbot-chatting-create.name}")
    private String chatBotChattingCreateEventTopic;

    public void send(ChatBotChattingCreateEvent event) {
        kafkaTemplate.send(chatBotChattingCreateEventTopic, event);
    }
}
