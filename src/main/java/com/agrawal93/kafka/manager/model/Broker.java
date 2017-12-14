package com.agrawal93.kafka.manager.model;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
public class Broker {
    
    private int id;
    private String host;
    private int port;
    private int jmx;
    
    private String endpoints[];

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getJmx() {
        return jmx;
    }

    public void setJmx(int jmx) {
        this.jmx = jmx;
    }

    public String[] getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(String[] endpoints) {
        this.endpoints = endpoints;
    }
    
}
