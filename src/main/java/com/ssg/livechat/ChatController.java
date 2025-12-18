package com.ssg.livechat;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 추가

    public ChatController(RedisPublisher redisPublisher, RedisTemplate<String, Object> redisTemplate) {
        this.redisPublisher = redisPublisher;
        this.redisTemplate = redisTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        // 1. 메시지를 Redis 리스트에 저장 (최근 100개만 유지하거나 만료시간 설정 가능)
        redisTemplate.opsForList().rightPush("chat_history", chatMessage);

        // [추가된 기능] 2. 데이터 관리 (저장소 용량 절약)
        // 리스트에 메시지가 200개가 넘어가면, 오래된 것부터 지워서 딱 200개만 유지합니다.
        // (보여주는 건 50개라도, 여유분으로 조금 더 저장해두는 것이 좋습니다.)
        redisTemplate.opsForList().trim("chat_history", -200, -1);

        redisPublisher.publish("chatroom", chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        redisPublisher.publish("chatroom", chatMessage);
    }
    // 2. 채팅 내역을 불러오는 API 추가 (REST)
    @GetMapping("/history")
    @ResponseBody
    public List<Object> getChatHistory() {
        // [수정된 기능] 3. 최근 50개만 가져오기
        // Redis의 range 명령어를 사용합니다. (0은 처음, -1은 끝)
        // -50은 "뒤에서 50번째"를 의미합니다. 즉, 뒤에서 50번째 ~ 맨 끝(-1)까지 가져옵니다.
        return redisTemplate.opsForList().range("chat_history", -50, -1);
    }


}

