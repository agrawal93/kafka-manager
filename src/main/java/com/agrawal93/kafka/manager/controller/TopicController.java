package com.agrawal93.kafka.manager.controller;

import com.agrawal93.kafka.manager.model.Topic;
import com.agrawal93.kafka.manager.service.TopicService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@RestController
public class TopicController {
    
    private static final String URL_PREFIX = "/clusters/{clusterId}/topics";
    
    @Autowired
    private TopicService service;
    
    @GetMapping(
            value = URL_PREFIX,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Topic> getAll(@PathVariable String clusterId) {
        return service.getAll(clusterId);
    }
    
    @GetMapping(
            value = URL_PREFIX,
            produces = MediaType.APPLICATION_JSON_VALUE,
            params = "names"
    )
    public Map<String, Topic> getSelected(@PathVariable String clusterId, @RequestParam("names") String topicNames) {
        return service.getSelected(clusterId, topicNames.split(","));
    }

    /*
    @GetMapping(
            value = URL_PREFIX + "/{brokerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Broker get(@PathVariable String clusterId, @PathVariable Integer brokerId) {
        return service.get(clusterId, brokerId);
    }
     */
}
