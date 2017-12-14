package com.agrawal93.kafka.manager.model;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
@Entity
public class Cluster implements Serializable {

    @Id
    private final String id = UUID.randomUUID().toString();

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String[] zookeepers;

    private boolean jmx;

    private String jmxUsername;

    private String jmxPassword;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getZookeepers() {
        return zookeepers;
    }

    public boolean isJmx() {
        return jmx;
    }

    public String getJmxUsername() {
        return jmxUsername;
    }

    public String getJmxPassword() {
        return jmxPassword;
    }

}
