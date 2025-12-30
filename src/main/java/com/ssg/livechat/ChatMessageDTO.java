package com.ssg.livechat;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long broadcastId;
    private Long memberId;
    private MessageType type;
    private String sender;
    private String content;
    private int vodPlayTime;
}
