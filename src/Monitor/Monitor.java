package Monitor;

import Communication.ClientAux;
import Communication.Message;
import Communication.ServerAux;
import Communication.TClientHandler;
import Server.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor extends  Thread implements IMonitor, IMonitor_Heartbeat{

    /** Server Information */
    private final Map<Integer, ServerInfo> servers;

    /** Requests waiting to be assigned to a server. */
    private final Map<Integer, List<Message> pendingRequests;
    private final String hostname;
    private final int port;
    private final ServerAux serverAux;
    private final Map<Integer, HeartbeatManager> heartbeatThreads;
    private final ReentrantLock rl;
    private int serverCount;
    private int lbCount;
    private int clientCount;
    private ClientAux primaryLB;

    public Monitor(String hostname, int port) {
        this.servers = new HashMap<Integer, ServerInfo>() ;
        this.rl = new ReentrantLock();
        this.hostname = hostname;
        this.port = port;
        this.pendingRequests = new HashMap<Integer, new ArrayList<Message>() ;
        this.heartbeatThreads = new HashMap<Integer,HeartbeatManager>();
        this.serverAux = new ServerAux(port);
    }

    public void run() {
        this.serverAux.start();
    }

    public void registerNewServer(ServerInfo serverInfo) {
        this.rl.lock();
        
        this.servers.put(this.serverCount, serverInfo);
        
        this.heartbeatThreads.put(this.serverCount, new HeartbeatManager(
        this.hostname, this.port, this.serverCount, this));
        this.heartbeatThreads.get(this.serverCount).start();

        this.serverCount++;

        this.rl.unlock();
    }

    public void registerLoadBalancer(ServerInfo serverInfo) {
        this.rl.lock();

        int id = this.lbCount++;
        
        this.servers.put(id, serverInfo);
        
        this.heartbeatThreads.put(id, new HeartbeatManager(
            this.hostname, this.port, id, this));
        this.heartbeatThreads.get(id).start();

        this.rl.unlock();

        // init connection with primary lb if it doesn't exist
        this.primaryLB = new ClientAux();

        // else add to list of secondary lb
        secondaries.add((id, port, host));
    }

    public void serverDown(int serverId){
        this.rl.lock();
        servers.remove(serverId);
        pendingRequests = pendingRequests.remove(serverId);
        heartbeatThreads.remove(serverId);
        this.rl.unlock();
        // add pending requests to message
        // send message to loadbalancer
        Message msg = new Message(pendingRequests);
        primaryLB.sendMsg(msg);
    }
    
    public void registerNewClient(Message msg) {
        this.rl.lock();
        
        int id = this.clientCount++;
        
        // store client info such as ports and put in the GUI
        this.clients.put()

        this.rl.unlock();
    }

    public void requestAck(Message msg){
        this.r1.lock();

        this.r1.unlock();
    }


    public void registerLoadBalancer() {

    }
}
