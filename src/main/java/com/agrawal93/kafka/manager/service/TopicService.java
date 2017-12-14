package com.agrawal93.kafka.manager.service;

import com.agrawal93.kafka.manager.model.Topic;
import com.agrawal93.kafka.manager.websocket.TopicSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import org.apache.commons.collections4.ListUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Service
public class TopicService {
    
    private static final String TOPIC_API = "/brokers/topics";
    
    @Autowired
    private ObjectMapper jsonMapper;
    
    @Autowired
    private ZookeeperService zookeeper;
    
    @Autowired
    private KafkaService kafka;
    
    @Autowired
    private TopicSocketHandler topicSocket;
    
    public Map<String, Topic> getAll(String clusterId) {
        AdminClient kafkaAdmin = kafka.getKafkaAdmin(clusterId);
        if (kafkaAdmin == null) {
            return null;
        }
        
        try {
            List<String> topicNames = getTopicNames(clusterId);
            Map<String, Topic> topics = new ConcurrentHashMap<>();
            List<List<String>> topicLists = ListUtils.partition(new ArrayList<>(topicNames), 100);
            topicLists
                    .parallelStream()
                    .map(topicList -> describeTopics(kafkaAdmin, topicList))
                    .forEach(topics::putAll);
            return topics;
        } finally {
            kafkaAdmin.close();
        }
    }
    
    public Map<String, Topic> getSelected(String clusterId, String topicNames[]) {
        AdminClient kafkaAdmin = kafka.getKafkaAdmin(clusterId);
        if (kafkaAdmin == null) {
            return null;
        }
        
        try {
            Map<String, Topic> topics = new ConcurrentHashMap<>();
            List<List<String>> topicLists = ListUtils.partition(Arrays.asList(topicNames), 100);
            topicLists
                    .parallelStream()
                    .map(topicList -> describeTopics(kafkaAdmin, topicList))
                    .forEach(topics::putAll);
            return topics;
        } finally {
            kafkaAdmin.close();
        }
    }
    
    private Map<String, Topic> describeTopics(AdminClient kafkaAdmin, Collection<String> topics) {
        try {
            return kafkaAdmin.describeTopics(topics).all()
                    .thenApply(new KafkaFuture.Function<Map<String, TopicDescription>, Map<String, Topic>>() {
                        
                        @Override
                        public Map<String, Topic> apply(Map<String, TopicDescription> topicDescriptions) {
                            Map<String, Topic> result = new TreeMap<>();
                            topicDescriptions.entrySet()
                                    .parallelStream()
                                    .map(entry -> entry.getValue())
                                    .map(topicDescription -> toTopic(topicDescription))
                                    .forEach(topic -> result.put(topic.getName(), topic));
                            
                            return result;
                        }
                        
                        private Topic toTopic(TopicDescription topicDescription) {
                            final Map<Integer, Object> brokers = new ConcurrentHashMap<>();
                            Topic.PartitionInfo partitions[] = topicDescription.partitions()
                                    .parallelStream()
                                    .map(partition -> toPartition(partition, brokers))
                                    .toArray(Topic.PartitionInfo[]::new);
                            
                            Topic topic = new Topic();
                            topic.setName(topicDescription.name());
                            topic.setBrokers(brokers.size());
                            topic.setPartitions(partitions);
                            return topic;
                        }
                        
                        private Topic.PartitionInfo toPartition(TopicPartitionInfo partition, Map<Integer, Object> brokers) {
                            brokers.put(partition.leader().id(), new Object());
                            Topic.PartitionInfo partitionInfo = new Topic.PartitionInfo();
                            partitionInfo.setIsr(nodeToIds(partition.isr()));
                            partitionInfo.setLeader(partition.leader().id());
                            partitionInfo.setPartition(partition.partition());
                            partitionInfo.setReplicas(nodeToIds(partition.replicas()));
                            return partitionInfo;
                        }
                        
                        private Integer[] nodeToIds(List<Node> nodes) {
                            return nodes.stream().map(node -> node.id()).toArray(Integer[]::new);
                        }
                        
                    }).get();
        } catch (InterruptedException | ExecutionException ex) {
            return new HashMap<>();
        }
    }
    
    public Topic get(String clusterId, String topicName) {
        ObjectNode topicNode = (ObjectNode) zookeeper.getData(clusterId, TOPIC_API + "/" + topicName, null);
        
        Topic.PartitionInfo topicPartitions[] = new Topic.PartitionInfo[topicNode.size()];
        IntStream.range(0, topicNode.size())
                .boxed()
                .parallel()
                .forEach(partitionId -> {
                    Topic.PartitionInfo partition = new Topic.PartitionInfo();
                    partition.setPartition(partitionId);
                    
                    try {
                        partition.setReplicas(jsonMapper.readValue(jsonMapper.writeValueAsString(topicNode.get(Integer.toString(partitionId))), Integer[].class));
                        ObjectNode partitionNode = (ObjectNode) zookeeper.getData(clusterId, TOPIC_API + "/" + topicName + "/partitions/" + partitionId + "/state", null);
                        partition.setLeader(partitionNode.get("leader").asInt());
                        partition.setIsr(jsonMapper.readValue(jsonMapper.writeValueAsString(partitionNode.get("isr")), Integer[].class));
                    } catch (IOException ex) {
                    }
                    
                    topicPartitions[partitionId] = partition;
                });
        
        Topic topic = new Topic();
        topic.setName(topicName);
        topic.setPartitions(topicPartitions);
        return topic;
    }
    
    private List<String> getTopicNames(String clusterId) {
        return zookeeper.getChildren(clusterId, TOPIC_API, null);
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
                    topicSocket.sendUpdate(clusterId, getTopicNames(clusterId));
                    break;
            }
        }
        
    }
    
}
