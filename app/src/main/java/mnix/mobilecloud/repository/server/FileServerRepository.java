package mnix.mobilecloud.repository.server;


import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.FileServer;

public class FileServerRepository {
    public static void save(Map<String, String> params) {
        FileServer fileServer = new FileServer();
        fileServer.setIdentifier(params.get("qquuid"));
        fileServer.setName(params.get("qqfilename"));
        Integer size = Integer.parseInt(params.get("qqtotalfilesize"));
        fileServer.setSize(size.longValue());
        Integer parts = Integer.parseInt(params.containsKey("qqtotalparts") ? params.get("qqtotalparts") : "1");
        fileServer.setParts(parts);
        fileServer.save();
    }

    public static List<FileServer> list() {
        return FileServer.listAll(FileServer.class);
    }
}
