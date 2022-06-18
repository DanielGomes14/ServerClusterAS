package Server;

import java.io.Serializable;

public class ServerInfo implements Serializable {
    private int serverId;
    private int serverPort;
    private int NI;
    private int activeReq;
    private int pendingReq;

    public ServerInfo(int serverId, int serverPort, int NI) {
        this.serverId = serverId;
        this.serverPort = serverPort;
        this.NI = NI;
        this.activeReq = 0;
        this.pendingReq = 0;
    }

    public int getPendingReq() {
        return pendingReq;
    }

    public void setPendingReq(int pendingReq) {
        this.pendingReq = pendingReq;
    }

    public int getActiveReq() {
        return activeReq;
    }

    public void setActiveReq(int activeReq) {
        this.activeReq = activeReq;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setNI(int NI) {
        this.NI = NI;
    }

    public int getServerId() {
        return serverId;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getNI() {
        return NI;
    }
}
