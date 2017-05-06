package mnix.mobilecloud.domain.server;

import com.orm.SugarRecord;

import java.util.Map;

import mnix.mobilecloud.util.Util;

public class FileServer extends SugarRecord {
    private String identifier;
    private String name;
    private Long size;
    private Integer segments;

    public FileServer() {
    }


    public FileServer(Map<String,String> params){
        this.setIdentifier(Util.cutUuid(params.get("qquuid")));
        this.setName(params.get("qqfilename"));
        Integer size = Integer.parseInt(params.get("qqtotalfilesize"));
        this.setSize(size.longValue());
        Integer parts = Integer.parseInt(params.containsKey("qqtotalparts") ? params.get("qqtotalparts") : "1");
        this.setSegments(parts);
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
