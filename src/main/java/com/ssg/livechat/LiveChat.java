package com.ssg.livechat;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "live_chat")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LiveChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false)
    private Long broadcastId;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType msgType;

    @Column(length = 500, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String sendNick;

    @Column(nullable = false)
    private boolean isWorld;

    @Column(nullable = false)
    private boolean isHidden;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sendLchat;

    @Column(nullable = false)
    private int vodPlayTime;
}