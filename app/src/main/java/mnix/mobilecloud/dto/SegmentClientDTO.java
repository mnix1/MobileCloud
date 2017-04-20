package mnix.mobilecloud.dto;

import mnix.mobilecloud.domain.client.SegmentClient;

public class SegmentClientDTO {
    private String identifier;
    private String fileIdentifier;
    private Long byteFrom;
    private Long byteTo;

    public SegmentClientDTO(SegmentClient segmentClient) {
        this.identifier = segmentClient.getIdentifier();
        this.fileIdentifier = segmentClient.getFileIdentifier();
        this.byteFrom = segmentClient.getByteFrom();
        this.byteTo = segmentClient.getByteTo();
    }
}
