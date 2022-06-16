package Server;

public class ServerInfo {
    private int serverId;
    private int serverPort;
    private int NI;

    public ServerInfo(int serverId, int serverPort, int NI) {
        this.serverId = serverId;
        this.serverPort = serverPort;
        this.NI = NI;
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
