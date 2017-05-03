package mnix.mobilecloud.algorithm;

import mnix.mobilecloud.algorithm.upload.HdfsBalancedFile;
import mnix.mobilecloud.algorithm.upload.HdfsDefault;
import mnix.mobilecloud.algorithm.upload.UploadPolicy;

public enum Algorithm {
    HDFS_DEFAULT,
    HDFS_BALANCED_FILE,
    HDFS_BALANCED_GLOBAL;


    public static UploadPolicy findUploadPolicy(String algorithmString) {
        Algorithm algorithm = algorithmString == null ? Algorithm.HDFS_DEFAULT : Algorithm.valueOf(algorithmString);
        if (algorithm == Algorithm.HDFS_DEFAULT) {
            return new HdfsDefault();
        }
        if (algorithm == Algorithm.HDFS_BALANCED_FILE) {
            return new HdfsBalancedFile();
        }
        return new HdfsDefault();
    }
}
