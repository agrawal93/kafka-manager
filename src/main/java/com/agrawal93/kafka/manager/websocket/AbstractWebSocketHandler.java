package com.agrawal93.kafka.manager.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Component
public abstract class AbstractWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    protected ObjectMapper jsonMapper;

    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        clients.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        clients.remove(session.getId());
    }

    protected void sendUpdate(Object object) throws IOException {
        final TextMessage message = new TextMessage(jsonMapper.writeValueAsBytes(object));
        clients.values()
                .parallelStream()
                .filter(session -> session != null && session.isOpen())
                .forEach(session -> {
                    synchronized (session) {
                        try {
                            session.sendMessage(message);
                        } catch (IOException ex) {
                        }
                    }
                });
    }

}
