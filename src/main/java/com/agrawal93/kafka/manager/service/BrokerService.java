package com.agrawal93.kafka.manager.service;

import com.agrawal93.kafka.manager.model.Broker;
import com.agrawal93.kafka.manager.websocket.BrokerSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Service
public class BrokerService {

    private static final String BROKER_API = "/brokers/ids";

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private ZookeeperService zookeeper;

    @Autowired
    private BrokerSocketHandler brokerSocket;

    public Map<Integer, Broker> getAll(String clusterId) {
        Map<Integer, Broker> brokers = new ConcurrentHashMap<>();
        getBrokerIds(clusterId)
                .parallelStream()
                .map(brokerId -> get(clusterId, Integer.parseInt(brokerId)))
                .filter(broker -> broker != null)
                .forEach(broker -> brokers.put(broker.getId(), broker));

        return brokers;
    }

    public Map<Integer, Broker> getSelected(String clusterId, String brokerIds[]) {
        Map<Integer, Broker> brokers = new ConcurrentHashMap<>();
        Stream.of(brokerIds)
                .parallel()
                .map(brokerId -> get(clusterId, Integer.parseInt(brokerId)))
                .filter(broker -> broker != null)
                .forEach(broker -> brokers.put(broker.getId(), broker));

        return brokers;
    }

    public Broker get(String clusterId, Integer brokerId) {
        try {
            ObjectNode brokerNode = (ObjectNode) zookeeper.getData(clusterId, BROKER_API + "/" + brokerId, null);

            Broker broker = new Broker();
            broker.setId(brokerId);
            broker.setHost(brokerNode.get("host").asText());
            broker.setPort(brokerNode.get("port").asInt());
            broker.setJmx(brokerNode.get("jmx_port").asInt());
            broker.setEndpoints(jsonMapper.readValue(jsonMapper.writeValueAsString(brokerNode.get("endpoints")), String[].class));
            return broker;
        } catch (IOException ex) {

        }
        return null;
    }

    private List<String> getBrokerIds(String clusterId) {
        return zookeeper.getChildren(clusterId, BROKER_API, new BrokerWatcher(clusterId));
    }

    private class BrokerWatcher implements Watcher {

        private final String clusterId;

        public BrokerWatcher(String clusterId) {
            this.clusterId = clusterId;
        }

        @Override
        public void process(WatchedEvent event) {
            switch (event.getType()) {
                case NodeChildrenChanged:
                    brokerSocket.sendUpdate(clusterId, getBrokerIds(clusterId));
                    break;
            }
        }

    }

}
