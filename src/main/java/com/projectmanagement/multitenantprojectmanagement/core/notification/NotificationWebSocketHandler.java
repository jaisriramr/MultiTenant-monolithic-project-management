package com.projectmanagement.multitenantprojectmanagement.core.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, List<WebSocketSession>> tenantSessions = new ConcurrentHashMap<>();
    

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String tenantId = getTenantId(session);
        tenantSessions.computeIfAbsent(tenantId, k -> new ArrayList<>()).add(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        tenantSessions.values().forEach(sessions -> sessions.remove(session));
    }

    public void sendToTenant(String tenantId, String message) throws IOException {
        List<WebSocketSession> sessions = tenantSessions.get(tenantId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    private String getTenantId(WebSocketSession session) {

        if(session.getUri() != null) {
            return session.getUri().getQuery().split("=")[1]; // e.g., ?tenantId=abc
        }
        return null;
    }

}
