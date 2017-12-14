package com.agrawal93.kafka.manager.websocket;

import com.agrawal93.kafka.manager.model.Cluster;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Component
public class ClusterSocketHandler extends AbstractWebSocketHandler {
    
    public static enum Event {
        CREATE, UPDATE, DELETE
    }
    
    public void sendUpdate(Event event, String clusterId, Cluster cluster) {
        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("event", event.name());
            node.put("clusterId", clusterId);
            node.put("cluster", jsonMapper.writeValueAsString(cluster));
            super.sendUpdate(node);
        } catch (IOException ex) {
        }
    }
    
}
