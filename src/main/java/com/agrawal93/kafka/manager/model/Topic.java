package com.agrawal93.kafka.manager.model;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
public class Topic {

    private String name;
    private PartitionInfo partitions[];
    private Integer brokers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PartitionInfo[] getPartitions() {
        return partitions;
    }

    public void setPartitions(PartitionInfo partitions[]) {
        this.partitions = partitions;
    }

    public Integer getBrokers() {
        return brokers;
    }

    public void setBrokers(Integer brokers) {
        this.brokers = brokers;
    }

    public static class PartitionInfo {

        private Integer isr[];
        private Integer leader;
        private Integer partition;
        private Integer replicas[];

        public Integer[] getIsr() {
            return isr;
        }

        public void setIsr(Integer isr[]) {
            this.isr = isr;
        }

        public Integer getLeader() {
            return leader;
        }

        public void setLeader(Integer leader) {
            this.leader = leader;
        }

        public Integer getPartition() {
            return partition;
        }

        public void setPartition(Integer partition) {
            this.partition = partition;
        }

        public Integer[] getReplicas() {
            return replicas;
        }

        public void setReplicas(Integer replicas[]) {
            this.replicas = replicas;
        }

    }

}
