package mnix.mobilecloud.option;

import java.util.Map;

import mnix.mobilecloud.algorithm.Algorithm;

public class Option {
    private static Option instance;

    private Long segmentSize = 1024L * 1024;
    private Integer replicaSize = 0;
    private Algorithm uploadAlgorithm = Algorithm.HDFS_DEFAULT;
    private Double speedFactor = 0d;
    private Double balancedPreference = 0.5;

    private Option() {
    }

    public static Option getInstance() {
        if (instance == null) {
            instance = new Option();
        }
        return instance;
    }

    public static void fromParams(Map<String, String> params) {
        instance.setSegmentSize(Long.parseLong(params.get("segmentSize")));
        instance.setReplicaSize(Integer.parseInt(params.get("replicaSize")));
        instance.setUploadAlgorithm(Algorithm.valueOf(params.get("uploadAlgorithm")));
        instance.setSpeedFactor(Double.parseDouble(params.get("speedFactor")));
        instance.setBalancedPreference(Double.parseDouble(params.get("balancedPreference")));
    }

    public Long getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(Long segmentSize) {
        this.segmentSize = segmentSize;
    }

    public Integer getReplicaSize() {
        return replicaSize;
    }

    public void setReplicaSize(Integer replicaSize) {
        this.replicaSize = replicaSize;
    }

    public Algorithm getUploadAlgorithm() {
        return uploadAlgorithm;
    }

    public void setUploadAlgorithm(Algorithm uploadAlgorithm) {
        this.uploadAlgorithm = uploadAlgorithm;
    }

    public Double getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(Double speedFactor) {
        this.speedFactor = speedFactor;
    }

    public Double getBalancedPreference() {
        return balancedPreference;
    }

    public void setBalancedPreference(Double balancedPreference) {
        this.balancedPreference = balancedPreference;
    }
}
