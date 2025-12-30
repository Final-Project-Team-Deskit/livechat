package com.ssg.livechat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveChatRepository extends JpaRepository<LiveChat, Long> {
    List<LiveChat> findByBroadcastIdOrderByMessageIdAsc(Long broadcastId);
}