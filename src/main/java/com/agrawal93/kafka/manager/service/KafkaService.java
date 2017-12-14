package com.agrawal93.kafka.manager.service;

import com.agrawal93.kafka.manager.model.Broker;
import com.agrawal93.kafka.manager.model.Cluster;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Service
public class KafkaService {

    @Autowired
    private Map<String, Map<String, Object>> registry;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private BrokerService brokerService;

    public AdminClient getKafkaAdmin(String clusterId) {
        if (registry.containsKey(clusterId)) {
            return KafkaAdminClient.create(registry.get(clusterId));
        }

        Cluster cluster = clusterService.get(clusterId);
        if (cluster != null) {
            Collection<Broker> brokers = brokerService.getAll(clusterId).values();

            LinkedList<String> bootstrapServers = new LinkedList<>();
            brokers
                    .parallelStream()
                    .filter(broker -> broker != null)
                    .map(broker -> broker.getHost() + ":" + broker.getPort())
                    .forEach(bootstrapServers::addFirst);

            if (bootstrapServers.isEmpty()) {
                return null;
            }

            Map<String, Object> adminConfiguration = new HashMap<>();
            adminConfiguration.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", bootstrapServers));
            adminConfiguration.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);

            registry.put(clusterId, adminConfiguration);

            AdminClient client = KafkaAdminClient.create(adminConfiguration);
            return client;
        }

        return null;
    }

}
