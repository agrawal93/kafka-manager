package com.agrawal93.kafka.manager.controller;

import com.agrawal93.kafka.manager.model.Broker;
import com.agrawal93.kafka.manager.service.BrokerService;
import java.util.Collection;
import java.util.List;
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
public class BrokerController {

    private static final String URL_PREFIX = "/clusters/{clusterId}/brokers";

    @Autowired
    private BrokerService service;

    @GetMapping(
            value = URL_PREFIX,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<Integer, Broker> getAll(@PathVariable String clusterId) {
        return service.getAll(clusterId);
    }

    @GetMapping(
            value = URL_PREFIX,
            produces = MediaType.APPLICATION_JSON_VALUE,
            params = "ids"
    )
    public Map<Integer, Broker> getSelected(@PathVariable String clusterId, @RequestParam("ids") String brokerIds) {
        return service.getSelected(clusterId, brokerIds.split(","));
    }

    @GetMapping(
            value = URL_PREFIX + "/{brokerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Broker get(@PathVariable String clusterId, @PathVariable Integer brokerId) {
        return service.get(clusterId, brokerId);
    }

}
