package com.agrawal93.kafka.manager.config;

import com.agrawal93.kafka.manager.model.Cluster;
import com.agrawal93.kafka.manager.model.repository.ClusterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Configuration
public class KafkaManagerConfig {

    @Value("${cluster.store}")
    private String clusterStore;

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    @Bean
    public File clusterStore() {
        return new File(clusterStore);
    }

    @Bean(autowire = Autowire.NO)
    public Object init(@Autowired ObjectMapper jsonMapper, @Autowired File clusterStore, @Autowired ClusterRepository clusterRepository) throws IOException {
        if (!clusterStore.exists()) {
            File clusterDirectory = clusterStore.getParentFile();
            if (!((clusterDirectory.exists() || clusterDirectory.mkdirs()) && clusterStore.createNewFile())) {
                throw new IOException("Unable to create cluster store.");
            }
        }

        if (clusterStore.length() == 0) {
            return null;
        }

        List<Cluster> clusters = jsonMapper.readValue(new FileInputStream(clusterStore), jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, Cluster.class));
        clusterRepository.save(clusters);

        return null;
    }

    @Bean
    public Map<String, ZooKeeper> zookeeperRegistry() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, Map<String, Object>> kafkaRegistry() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Watcher defaultZookeeperWatcher() {
        return new Watcher() {
            @Override
            public void process(WatchedEvent we) {
            }
        };
    }

}
