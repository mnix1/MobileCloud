package mnix.mobilecloud.algorithm;

import mnix.mobilecloud.algorithm.upload.DefaultBlockPlacementPolicy;
import mnix.mobilecloud.algorithm.upload.UploadPolicy;

public enum Algorithm {
    DEFAULT_BLOCK_PLACEMENT_POLICY;


    public static UploadPolicy findUploadPolicy(String algorithmString) {
        Algorithm algorithm = algorithmString == null ? Algorithm.DEFAULT_BLOCK_PLACEMENT_POLICY : Algorithm.valueOf(algorithmString);
        if (algorithm == Algorithm.DEFAULT_BLOCK_PLACEMENT_POLICY) {
            return new DefaultBlockPlacementPolicy();
        }
        return new DefaultBlockPlacementPolicy();
    }
}
