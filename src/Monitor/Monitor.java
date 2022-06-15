package Monitor;

import Communication.Message;
import Communication.ServerAux;
import Communication.TClientHandler;
import Server.ServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor extends  Thread implements IMonitor{

    /** Server Information */
    private final Map<Integer, ServerInfo> servers;

    /** Requests waiting to be assigned to a server. */
    private final Map<Integer, String> pendingRequests;
    private final String hostname;
    private final int port;
    private final ServerAux serverAux;
    private final Map<Integer, HeartbeatManager> heartbeatThreads;
    private final ReentrantLock rl;
    public Monitor(String hostname, int port) {
        this.servers = new HashMap<Integer, ServerInfo>() ;
        this.rl = new ReentrantLock();
        this.hostname = hostname;
        this.port = port;
        this.pendingRequests = new HashMap<Integer, String>() ;
        this.heartbeatThreads = new HashMap<Integer,HeartbeatManager>();
        this.serverAux = new ServerAux(port);

    }

    public void run() {
        this.serverAux.start();
    }

    public void registerNewServer(int serverId, ServerInfo serverInfo) {
        this.rl.lock();
        this.servers.put(serverId, serverInfo);
        this.heartbeatThreads.put(serverId, new HeartbeatManager(
                this.hostname, this.port, serverId, this));
        this.heartbeatThreads.get(serverId).start();
        // Inform new Server to LoadBalancer
        this.rl.unlock();
    }

    public void serverDown(int serverId){
        this.rl.lock();
        Message msg = new Message();
        // add pending requests to message
        // send message to loadbalancer
        servers.remove(serverId);
        pendingRequests.remove(serverId);
        heartbeatThreads.remove(serverId);
        this.rl.unlock();

    }



    public void registerLoadBalancer() {

    }

}
