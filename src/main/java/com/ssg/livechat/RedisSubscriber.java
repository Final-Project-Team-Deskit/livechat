package com.ssg.livechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisSubscriber(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Redis에서 메시지가 발행(Publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     * 여기서 메시지는 "String(JSON 문자열)"로 들어옵니다.
     */
    public void receiveMessage(String message) {
        try {
            // 1. 들어온 JSON 문자열을 ChatMessage 객체로 변환 (역직렬화)
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);

            logger.info("Received message from Redis: {}", chatMessage.getContent());

            // 2. 채팅방(WebSocket)에 있는 클라이언트들에게 메시지 전달
            messagingTemplate.convertAndSend("/topic/public", chatMessage);

        } catch (Exception e) {
            logger.error("Exception in RedisSubscriber", e);
        }
    }
}