package com.agrawal93.kafka.manager.websocket;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Component
public class BrokerSocketHandler extends AbstractWebSocketHandler {

    public void sendUpdate(String clusterId, List<String> brokerIds) {
        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("cluster", clusterId);
            node.putArray("brokers");

            brokerIds
                    .parallelStream()
                    .forEach(((ArrayNode) node.get("brokers"))::add);

            super.sendUpdate(node);
        } catch (IOException ex) {
        }
    }

}
