package backend;

import java.util.Arrays;

public class CurrentUser {
    private String nodeID,name,email;
    private String[] filesShared,filesDownloaded;

    public String getNodeID() {
        return nodeID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String[] getFilesShared() {
        return filesShared;
    }

    public String[] getFilesDownloaded() {
        return filesDownloaded;
    }

    public CurrentUser(String nodeID, String name, String email, String[] filesShared, String[] filesDownloaded) {
        this.nodeID = nodeID;
        this.name = name;
        this.email = email;
        this.filesShared = filesShared;
        this.filesDownloaded = filesDownloaded;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "nodeID='" + nodeID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", filesShared=" + Arrays.toString(filesShared) +
                ", filesDownloaded=" + Arrays.toString(filesDownloaded) +
                '}';
    }

    public CurrentUser(String nodeID){
        this.nodeID=nodeID;
        this.name = null;
        this.email = null;
        this.filesShared = null;
        this.filesDownloaded = null;
    }
}
