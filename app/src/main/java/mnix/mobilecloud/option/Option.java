package mnix.mobilecloud.option;

import java.util.Map;

import mnix.mobilecloud.algorithm.balance.BalanceAlgorithm;
import mnix.mobilecloud.algorithm.upload.UploadAlgorithm;

public class Option {
    private static Option instance;

    private Long segmentSize = 1024L * 1024;
    private Integer replicaSize = 0;
    private UploadAlgorithm uploadAlgorithm = UploadAlgorithm.HDFS_DEFAULT;
    private BalanceAlgorithm balanceAlgorithm = BalanceAlgorithm.HDFS_BALANCER;
    private Double speedFactor = 0d;
    private Double balancedPreference = 0.5;
    private Double utilizationThreshold = 0.1;
    private Integer dhtModulo = 4096;

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
        instance.setUploadAlgorithm(UploadAlgorithm.valueOf(params.get("uploadAlgorithm")));
        instance.setBalanceAlgorithm(BalanceAlgorithm.valueOf(params.get("balanceAlgorithm")));
        instance.setSpeedFactor(Double.parseDouble(params.get("speedFactor")));
        instance.setBalancedPreference(Double.parseDouble(params.get("balancedPreference")));
        instance.setUtilizationThreshold(Double.parseDouble(params.get("utilizationThreshold")));
        instance.setDhtModulo(Integer.parseInt(params.get("dhtModulo")));
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

    public UploadAlgorithm getUploadAlgorithm() {
        return uploadAlgorithm;
    }

    public void setUploadAlgorithm(UploadAlgorithm uploadAlgorithm) {
        this.uploadAlgorithm = uploadAlgorithm;
    }

    public BalanceAlgorithm getBalanceAlgorithm() {
        return balanceAlgorithm;
    }

    public void setBalanceAlgorithm(BalanceAlgorithm balanceAlgorithm) {
        this.balanceAlgorithm = balanceAlgorithm;
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

    public Double getUtilizationThreshold() {
        return utilizationThreshold;
    }

    public void setUtilizationThreshold(Double utilizationThreshold) {
        this.utilizationThreshold = utilizationThreshold;
    }

    public Integer getDhtModulo() {
        return dhtModulo;
    }

    public void setDhtModulo(Integer dhtModulo) {
        this.dhtModulo = dhtModulo;
    }
}
