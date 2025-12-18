package com.ssg.livechat;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final RedisTemplate<String, Object> redisTemplate; // 채팅 내역(JSON) 저장용
    private final StringRedisTemplate stringRedisTemplate;     // [추가] 방 목록(String) 저장용

    // 생성자에 StringRedisTemplate 추가
    public ChatController(RedisPublisher redisPublisher,
                          RedisTemplate<String, Object> redisTemplate,
                          StringRedisTemplate stringRedisTemplate) {
        this.redisPublisher = redisPublisher;
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 1. 방 목록 불러오기 (StringRedisTemplate 사용)
    @GetMapping("/rooms")
    @ResponseBody
    public Set<String> getActiveRooms() {
        // 단순 문자열 처리에 특화된 template을 사용하므로 에러가 나지 않습니다.
        return stringRedisTemplate.opsForSet().members("active_rooms");
    }

    // 2. 방송 생성 알림 & 방 저장
    @MessageMapping("/chat.create")
    public void createRoom(@Payload ChatMessage chatMessage) {
        // 방 이름을 StringRedisTemplate으로 저장
        stringRedisTemplate.opsForSet().add("active_rooms", chatMessage.getRoomId());

        chatMessage.setType(ChatMessage.MessageType.CREATE);
        chatMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        redisPublisher.publish("chatroom", chatMessage);
    }

    // 3. 메시지 전송
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        String historyKey = "chat_history:" + chatMessage.getRoomId();
        redisTemplate.opsForList().rightPush(historyKey, chatMessage);
        redisTemplate.opsForList().trim(historyKey, -200, -1);

        redisPublisher.publish("chatroom", chatMessage);
    }

    // 4. 입장 알림
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        redisPublisher.publish("chatroom", chatMessage);
    }

    // 5. 채팅 내역 불러오기 (기존 유지)
    @GetMapping("/history/{roomId}")
    @ResponseBody
    public List<Object> getChatHistory(@PathVariable String roomId) {
        return redisTemplate.opsForList().range("chat_history:" + roomId, -50, -1);
    }
}