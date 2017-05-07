package mnix.mobilecloud.algorithm;

import mnix.mobilecloud.algorithm.upload.Hadaps;
import mnix.mobilecloud.algorithm.upload.HdfsBalancedFile;
import mnix.mobilecloud.algorithm.upload.HdfsBalancedGlobal;
import mnix.mobilecloud.algorithm.upload.HdfsDefault;
import mnix.mobilecloud.algorithm.upload.UploadPolicy;

public enum Algorithm {
    HDFS_DEFAULT,
    HDFS_BALANCED_FILE,
    HDFS_BALANCED_GLOBAL,
    HADAPS;


    public static UploadPolicy findUploadPolicy(Algorithm algorithm) {
        if (algorithm == Algorithm.HDFS_DEFAULT) {
            return new HdfsDefault();
        }
        if (algorithm == Algorithm.HDFS_BALANCED_FILE) {
            return new HdfsBalancedFile();
        }
        if (algorithm == Algorithm.HDFS_BALANCED_GLOBAL) {
            return new HdfsBalancedGlobal();
        }
        if (algorithm == Algorithm.HADAPS) {
            return new Hadaps();
        }
        return new HdfsDefault();
    }
}
