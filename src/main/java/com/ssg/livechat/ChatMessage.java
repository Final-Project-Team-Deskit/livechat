package com.ssg.livechat;

public class ChatMessage {

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        CREATE // [추가] 방송(방) 생성 알림용
    }

    private MessageType type;
    private String content;
    private String sender;
    private String time;
    private String roomId;

    // Getters and Setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}