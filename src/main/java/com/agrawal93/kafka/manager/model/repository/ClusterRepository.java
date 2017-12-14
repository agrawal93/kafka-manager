package com.agrawal93.kafka.manager.model.repository;

import com.agrawal93.kafka.manager.model.Cluster;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
public interface ClusterRepository extends CrudRepository<Cluster, String> {
    
    public Cluster findByName(String name);
    
}
