package mnix.mobilecloud.domain.server;

import com.orm.SugarRecord;

public class FileServer extends SugarRecord {
    private String identifier;
    private String name;
    private Long size;
    private Integer segments;

    public FileServer() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getSegments() {
        return segments;
    }

    public void setSegments(Integer segments) {
        this.segments = segments;
    }
}
