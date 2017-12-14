package com.agrawal93.kafka.manager.service;

import com.agrawal93.kafka.manager.exception.AlreadyExistsException;
import com.agrawal93.kafka.manager.exception.InvalidPayloadException;
import com.agrawal93.kafka.manager.exception.NotFoundException;
import com.agrawal93.kafka.manager.model.Cluster;
import com.agrawal93.kafka.manager.model.repository.ClusterRepository;
import com.agrawal93.kafka.manager.websocket.ClusterSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Service
public class ClusterService {

    @Autowired
    private ClusterRepository repository;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private File clusterStore;

    @Autowired
    private ClusterSocketHandler socket;

    // READ ALL
    public List<Cluster> getAll() {
        List<Cluster> clusters = new ArrayList<>((int) repository.count());
        repository.findAll().forEach(clusters::add);
        return clusters;
    }

    // READ
    public Cluster get(String id) {
        Cluster cluster = repository.findOne(id);
        cluster = cluster == null ? repository.findByName(id) : cluster;

        if (cluster == null) {
            throw new NotFoundException("Cluster [" + id + "] Not Found.");
        }

        return cluster;
    }

    // DELETE
    public void delete(String id) throws IOException {
        repository.delete(get(id));
        _updateClusterStore();
        socket.sendUpdate(ClusterSocketHandler.Event.DELETE, id, null);
    }

    // CREATE
    public Cluster create(Cluster cluster) throws IOException {
        if (cluster.getId() != null && repository.exists(cluster.getId())) {
            throw new AlreadyExistsException("Cluster [" + cluster.getId() + "] Already Exists.");
        }

        cluster = repository.save(cluster);
        _updateClusterStore();
        socket.sendUpdate(ClusterSocketHandler.Event.CREATE, cluster.getId(), cluster);
        return cluster;
    }

    // UPDATE
    public Cluster update(String id, Cluster cluster) throws IOException {
        if (id != null && !id.equals(cluster.getId()) && !id.equals(cluster.getName())) {
            throw new InvalidPayloadException("Id and Cluster information do not match.");
        }
        get(id);

        cluster = repository.save(cluster);
        _updateClusterStore();
        socket.sendUpdate(ClusterSocketHandler.Event.UPDATE, cluster.getId(), cluster);
        return cluster;
    }

    private void _updateClusterStore() throws IOException {
        jsonMapper.writeValue(clusterStore, getAll());
    }

}
