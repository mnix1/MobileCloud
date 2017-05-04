package mnix.mobilecloud.option;

import java.util.Map;

import mnix.mobilecloud.algorithm.Algorithm;

public class Option {
    private static Option instance;

    private Long segmentSize = 1024L * 1024;
    private Algorithm uploadAlgorithm = Algorithm.HDFS_DEFAULT;
    private Double speedFactor = 3d;

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
        instance.setUploadAlgorithm(Algorithm.valueOf(params.get("uploadAlgorithm")));
        instance.setSpeedFactor(Double.parseDouble(params.get("speedFactor")));
    }

    public Long getSegmentSize() {
        return segmentSize;
    }

    public void setSegmentSize(Long segmentSize) {
        this.segmentSize = segmentSize;
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
}
