package com.agrawal93.kafka.manager.config;

import com.agrawal93.kafka.manager.websocket.BrokerSocketHandler;
import com.agrawal93.kafka.manager.websocket.ClusterSocketHandler;
import com.agrawal93.kafka.manager.websocket.TopicSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ClusterSocketHandler clusterSocket;

    @Autowired
    private BrokerSocketHandler brokerSocket;

    @Autowired
    private TopicSocketHandler topicSocket;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(clusterSocket, "/ws/clusters");
        registry.addHandler(brokerSocket, "/ws/brokers");
        registry.addHandler(topicSocket, "/ws/topics");
    }

}
