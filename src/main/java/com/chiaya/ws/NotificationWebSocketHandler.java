package com.chiaya.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** userId -> 该用户的所有 session（一个用户可能多标签页） */
    private static final Map<Long, Set<WebSocketSession>> USER_SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(JwtHandshakeInterceptor.ATTR_USER_ID);
        if (userId == null) {
            try { session.close(); } catch (IOException ignored) { }
            return;
        }
        USER_SESSIONS.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("WS 连接建立 userId={}, sessionId={}", userId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 只处理心跳
        String payload = message.getPayload();
        if (payload != null && payload.contains("\"PING\"")) {
            session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(JwtHandshakeInterceptor.ATTR_USER_ID);
        if (userId != null) {
            Set<WebSocketSession> set = USER_SESSIONS.get(userId);
            if (set != null) {
                set.remove(session);
                if (set.isEmpty()) USER_SESSIONS.remove(userId);
            }
        }
        log.info("WS 连接关闭 userId={}, sessionId={}", userId, session.getId());
    }

    /**
     * 给指定用户推送消息
     */
    public void sendToUser(Long userId, Object payload) {
        Set<WebSocketSession> sessions = USER_SESSIONS.get(userId);
        if (sessions == null || sessions.isEmpty()) return;
        String json;
        try {
            json = MAPPER.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("序列化 WS 消息失败", e);
            return;
        }
        TextMessage msg = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    synchronized (s) {
                        s.sendMessage(msg);
                    }
                } catch (IOException e) {
                    log.warn("WS 发送失败 sessionId={}", s.getId(), e);
                }
            }
        }
    }
}
