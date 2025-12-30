package com.ssg.livechat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController // REST API와 WebSocket 처리를 위해 RestController/Controller 혼합 사용 가능
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    /* [REST API] 1. 테스트용 방 생성 */
    // 판매자 연동 전까지 브라우저에서 방을 생성하기 위한 용도
    @PostMapping("/api/chat/room")
    public Long createRoom(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        return chatService.createTestRoom(name);
    }

    /* [REST API] 2. 전체 방 목록 조회 */
    // 현재 생성된 방송 번호(broadcast_id) 목록을 가져옴
    @GetMapping("/api/chat/rooms")
    public Set<Long> getRoomList() {
        return chatService.findAllRooms();
    }

    /* [STOMP] 3. 채팅 메시지 처리 */
    @MessageMapping("/chat/message")
    public void handleMessage(ChatMessageDTO message) {
        // 1. 금칙어 필터링
        String filtered = chatService.filterContent(message.getContent());
        message.setContent(filtered);

        // 2. DB 비동기 저장 (유저님 요청사항)
        chatService.saveMessageAsync(message);

        // 3. 실시간 전파 (/sub/chat/{broadcastId})
        messagingTemplate.convertAndSend("/sub/chat/" + message.getBroadcastId(), message);
    }
}