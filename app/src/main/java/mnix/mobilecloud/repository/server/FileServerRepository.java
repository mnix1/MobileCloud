package mnix.mobilecloud.repository.server;


import java.util.List;
import java.util.Map;

import mnix.mobilecloud.domain.server.FileServer;
import mnix.mobilecloud.domain.server.SegmentServer;
import mnix.mobilecloud.util.Util;

public class FileServerRepository {
    public static void clear(){
        FileServer.deleteAll(FileServer.class);
    }

    public static List<FileServer> list() {
        return FileServer.listAll(FileServer.class);
    }

    public static FileServer findByIdentifier(String identifier) {
        List<FileServer> fileServers = FileServer.find(FileServer.class, "identifier = ?", identifier);
        if (fileServers.size() > 1) {
            throw new IndexOutOfBoundsException("Invalid identifier (not unique)");
        } else if (fileServers.size() == 0) {
            return null;
        }
        return fileServers.get(0);
    }
}
