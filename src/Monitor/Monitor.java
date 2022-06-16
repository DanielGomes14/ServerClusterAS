package Monitor;

import Communication.ClientAux;
import Communication.Message;
import Communication.ServerAux;
import Server.ServerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor implements IMonitor, IMonitor_Heartbeat{

    /** Server Information */
    private final Map<Integer, ServerInfo> servers;

    /** Requests waiting to be assigned to a server. */
    private final Map<Integer, List<Message>> pendingRequests;
    private final String hostname = "localhost";
    private final int port = 9000;
    private final ServerAux serverAux;
    private final Map<Integer, HeartbeatManager> heartbeatThreads;
    private final ReentrantLock rl;
    private int serverCount;
    private int lbCount;
    private int clientCount;
    private List<ClientAux> LBs;

    public Monitor() {
        this.rl = new ReentrantLock();
        this.servers = new HashMap<>();
        this.pendingRequests = new HashMap<>();
        this.heartbeatThreads = new HashMap<>();
        this.serverAux = new ServerAux();
        this.LBs = new ArrayList<>();
    }

    public void start() {
        this.serverAux.start(port);
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

    public  void receiveNewRequest(Message message){
        this.rl.lock();
        List<Message> lstmessages;
        if(this.pendingRequests.containsKey(message.getRequestId())){
            lstmessages = this.pendingRequests.get(message.getRequestId());
            lstmessages.add(message);
        }
        else{
            lstmessages =  new ArrayList<Message>();
            lstmessages.add(message);
        }
        this.pendingRequests.put(message.getRequestId(), lstmessages);

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

        this.LBs.add(new ClientAux(this.hostname, port));
    }

    public void serverDown(int serverId){
        this.rl.lock();

        servers.remove(serverId);
        List<Message> pendingRequests = this.pendingRequests.remove(serverId);
        heartbeatThreads.remove(serverId);

        this.rl.unlock();

        // add pending requests to message
        // send message to loadbalancer
        Message msg = new Message();
        ClientAux primaryLb = this.LBs.get(0);
        if (primaryLb != null)
            primaryLb.sendMsg(msg);
    }
    
    public void registerNewClient(Message msg) {
        this.rl.lock();
        
        int id = this.clientCount++;
        
        // store client info such as ports and put in the GUI
//        this.clients.put();

        this.rl.unlock();
    }

    public void requestProcessed(Message msg){
        this.rl.lock();

        this.rl.unlock();
    }


    public void registerLoadBalancer() {
        this.rl.lock();

        int id = this.clientCount++;

        // add this lb id to the list, maybe create a loadbalancersinfo class aswell
        // with id and ClientAux connection
        // but here only add the id
//        this.LBs.add();

        this.rl.unlock();
    }

    public static void main(String[] args) {
        new Monitor();
    }

}
