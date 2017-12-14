package com.agrawal93.kafka.manager.controller;

import com.agrawal93.kafka.manager.model.Cluster;
import com.agrawal93.kafka.manager.service.ClusterService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@RestController
public class ClusterController {

    private static final String URL_PREFIX = "/clusters";

    @Autowired
    private ClusterService service;

    @GetMapping(
            value = URL_PREFIX,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Cluster> getAll() {
        return service.getAll();
    }

    @PostMapping(
            value = URL_PREFIX,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Cluster create(@RequestBody Cluster cluster) throws IOException {
        return service.create(cluster);
    }

    @GetMapping(
            value = URL_PREFIX + "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Cluster get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping(
            value = URL_PREFIX + "/{id}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Cluster update(@PathVariable String id, @RequestBody Cluster cluster) throws IOException {
        return service.update(id, cluster);
    }

    @DeleteMapping(URL_PREFIX + "/{id}")
    public void delete(@PathVariable String id) throws IOException {
        service.delete(id);
    }

}
