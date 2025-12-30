package com.ssg.livechat;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class ChatService {
    private final LiveChatRepository liveChatRepository;
    private final ForbiddenWordRepository forbiddenWordRepository;
    // 테스트용 방 목록 저장소 (실제 운영 시에는 Redis나 DB의 Broadcast 테이블 사용)
    private final Set<Long> activeRooms = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private List<ForbiddenWord> cachedWords; // 메모리 캐싱으로 성능 향상

    @PostConstruct
    public void init() {
        this.cachedWords = forbiddenWordRepository.findAll();
    }
    // [추가] 테스트용 방 생성 로직
    public Long createTestRoom(String name) {
        // 임의의 방송 번호 생성 (랜덤 ID)
        Long broadcastId = (long) (Math.random() * 10000);
        activeRooms.add(broadcastId);
//        log.info("Test Room Created: {} (ID: {})", name, broadcastId);
        return broadcastId;
    }

    // [추가] 방 목록 조회
    public Set<Long> findAllRooms() {
        return activeRooms;
    }

    // 1. 금칙어 필터링 (동기 방식 - 메시지 발송 전 처리 필요)
    public String filterContent(String content) {
        for (ForbiddenWord fw : cachedWords) {
            if (content.contains(fw.getWord())) {
                content = content.replace(fw.getWord(), fw.getReplacement());
            }
        }
        return content;
    }

    // 2. DB 저장 (비동기 방식 - 저장 결과와 상관없이 채팅 전파를 위해)
    @Async("chatSaveExecutor")
    public void saveMessageAsync(ChatMessageDTO dto) {
        LiveChat entity = LiveChat.builder()
                .broadcastId(dto.getBroadcastId()) // 판매자가 만든 방송 번호 매핑
                .memberId(dto.getMemberId())
                .msgType(dto.getType())
                .content(dto.getContent())
                .sendNick(dto.getSender())
                .isWorld(false)
                .isHidden(false)
                .vodPlayTime(dto.getVodPlayTime())
                .build();

        liveChatRepository.save(entity);
    }
}