package backend;

public class ActivePeer {
    private String ipAddress,nodeID;
    private int port;
    private String[] filesDownloaded,filesShared;

    public ActivePeer() {}

    public String getIpAddress() {
        return ipAddress;
    }

    public String getNodeID() {
        return nodeID;
    }

    public int getPort() {
        return port;
    }
}
