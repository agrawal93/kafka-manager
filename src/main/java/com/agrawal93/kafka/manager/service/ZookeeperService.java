package com.agrawal93.kafka.manager.service;

import com.agrawal93.kafka.manager.model.Cluster;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Service
public class ZookeeperService {

    @Value("${zookeeper.session.timeout}")
    private Integer zookeeperSessionTimeout;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private Map<String, ZooKeeper> registry;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private Watcher defaultWatcher;

    public List<String> getChildren(String clusterId, String path, Watcher watcher) {
        ZooKeeper zookeeper = getZookeeper(clusterId);
        if (zookeeper == null) {
            return null;
        }

        try {
            return zookeeper.getChildren(path, watcher);
        } catch (InterruptedException | KeeperException ex) {
            return new ArrayList<>();
        }
    }

    public JsonNode getData(String clusterId, String path, Watcher watcher) {
        ZooKeeper zookeeper = getZookeeper(clusterId);
        if (zookeeper == null) {
            return null;
        }

        try {
            return jsonMapper.readTree(new String(zookeeper.getData(path, watcher, null)));
        } catch (InterruptedException | KeeperException | IOException ex) {
            return null;
        }
    }

    public ZooKeeper getZookeeper(String clusterId) {
        if (registry.containsKey(clusterId) && registry.get(clusterId).getState() == ZooKeeper.States.CONNECTED) {
            return registry.get(clusterId);
        }

        try {
            Cluster cluster = clusterService.get(clusterId);
            if (cluster != null) {
                ZooKeeper zookeeper = new ZooKeeper(String.join(",", cluster.getZookeepers()), zookeeperSessionTimeout, defaultWatcher);
                registry.put(clusterId, zookeeper);
                return zookeeper;
            }
        } catch (IOException ex) {

        }
        return null;
    }

}
