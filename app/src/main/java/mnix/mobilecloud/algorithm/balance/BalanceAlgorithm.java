package mnix.mobilecloud.algorithm.balance;

public enum BalanceAlgorithm {
    HDFS_BALANCER,
    DISTRIBUTED_HASH_TABLE,
    HADAPS;


    public static BalancePolicy findBalancePolicy(BalanceAlgorithm balanceAlgorithm) {
        if (balanceAlgorithm == BalanceAlgorithm.HDFS_BALANCER) {
            return new HdfsBalancer();
        }
        if (balanceAlgorithm == BalanceAlgorithm.DISTRIBUTED_HASH_TABLE) {
            return new DistributedHashTableBalancer();
        }
        return new HdfsBalancer();
    }
}
