package mnix.mobilecloud.algorithm.upload;

public enum UploadAlgorithm {
    HDFS_DEFAULT,
    HDFS_BALANCED_FILE,
    HDFS_BALANCED_GLOBAL,
    HADAPS,
    DISTRIBUTED_HASH_TABLE;


    public static UploadPolicy findUploadPolicy(UploadAlgorithm uploadAlgorithm) {
        if (uploadAlgorithm == UploadAlgorithm.HDFS_DEFAULT) {
            return new HdfsDefault();
        }
        if (uploadAlgorithm == UploadAlgorithm.HDFS_BALANCED_FILE) {
            return new HdfsBalancedFile();
        }
        if (uploadAlgorithm == UploadAlgorithm.HDFS_BALANCED_GLOBAL) {
            return new HdfsBalancedGlobal();
        }
        if (uploadAlgorithm == UploadAlgorithm.HADAPS) {
            return new Hadaps();
        }
        if (uploadAlgorithm == UploadAlgorithm.DISTRIBUTED_HASH_TABLE) {
            return new DistributedHashTable();
        }
        return new HdfsDefault();
    }
}
