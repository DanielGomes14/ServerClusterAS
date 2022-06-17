package Monitor;

import Communication.ClientAux;
import Communication.Message;
import Server.ServerInfo;

import java.io.IOException;
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
    private final int port = 5000;
    private final ServerAux serverAux;
    private final Map<Integer, HeartbeatManager> serverHeartbeatThreads;
    private final Map<Integer, HeartbeatManager> LBHeartbeatThreads;
    private final ReentrantLock rl;
    private int serverCount;
    private int LBCount;
    private int clientCount;
    private final MonitorGUI gui;


    private final Map<Integer, ClientAux> LBs;
    private int primaryLB;

    public Monitor() {
        this.rl = new ReentrantLock();
        this.servers = new HashMap<>();
        this.LBs = new HashMap<>();
        this.primaryLB = -1;

        this.pendingRequests = new HashMap<>();
        this.serverHeartbeatThreads = new HashMap<>();
        this.LBHeartbeatThreads = new HashMap<>();

        this.serverAux = new ServerAux(this, port);
        this.serverAux.start();

        this.gui = new MonitorGUI(this);
    }

    public MonitorGUI getGui() {
        return this.gui;
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

    public int registerLoadBalancer(Message msg) {
        int id = -1;

        this.rl.lock();

        if (this.LBs.size() <= 2) {
            id = this.LBCount++;

            ClientAux con = new ClientAux(this.hostname, msg.getServerPort());
            con.start();

            this.LBs.put(id, con);

            if (this.primaryLB == -1) {
                // send pending requests
                this.primaryLB = id;
            }

            this.LBHeartbeatThreads.put(id, new HeartbeatManager(
                this.hostname, msg.getServerPort(), id, false, this));
            this.LBHeartbeatThreads.get(id).start();
        }

        this.rl.unlock();

        return id;
    }

    public void registerNewServer(ServerInfo serverInfo) {
        this.rl.lock();

        int id = this.LBCount++;

        serverInfo.setServerId(id);

        this.servers.put(id, serverInfo);

        this.serverHeartbeatThreads.put(id, new HeartbeatManager(
                this.hostname, serverInfo.getServerPort(), id, true, this));
        this.serverHeartbeatThreads.get(id).start();

        this.rl.unlock();
    }

    public void serverDown(int serverId){
        this.rl.lock();

        servers.remove(serverId);
        List<Message> pendingRequests = this.pendingRequests.remove(serverId);
        serverHeartbeatThreads.remove(serverId);

        this.rl.unlock();

        // add pending requests to message
        // send message to loadbalancer
        Message msg = new Message();
        ClientAux LB = this.LBs.get(primaryLB);
        if (LB != null) {
            try {
                LB.sendMsg(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void LBDown(int LBId){
        this.rl.lock();

        LBHeartbeatThreads.remove(LBId);
        
        this.LBs.remove(LBId);

        if (LBId == primaryLB){
            primaryLB = -1;

            // send pending requests to other active lb
            if (! this.LBs.isEmpty()) {
                Integer activeLBId = (Integer) this.LBs.keySet().toArray()[0];
                if (activeLBId != null) {
                    primaryLB = activeLBId;
                    // send pending
                }
            }
        }

        this.rl.unlock();
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


    public Map<Integer,ServerInfo> getServersInfo(){
        return servers;
    }
    public static void main(String[] args) {
        new Monitor();
    }

}
