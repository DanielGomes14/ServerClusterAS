package Monitor;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import Server.ServerInfo;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor implements IMonitor, IMonitor_Heartbeat{

    /** Server Information */
    private final Map<Integer, ServerInfo> servers;

    /** Requests waiting to be assigned to a server. */
    private final Map<Integer, Message> pendingRequests;
    private final String hostname = "localhost";
    private final int port = 5000;
    private final ServerAux serverAux;
    private final Map<Integer, HeartbeatManager> serverHeartbeatThreads;
    private final Map<Integer, HeartbeatManager> LBHeartbeatThreads;
    private final ReentrantLock rl;
    private int serverCount = 0;
    private int LBCount = 0;
    private int clientCount = 0;
    private final Map<Integer, Integer> clients;
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

        this.clients = new HashMap<>();

        this.gui = new MonitorGUI(this);
    }


    public  void receiveNewRequest(Message msg){
        this.rl.lock();

        this.gui.addPendingRequest(msg);

        if (clients.containsKey(msg.getClientId())) {
            msg.setServerPort(clients.get(msg.getClientId()));
        }
        // send information of current servers
        // plus the request itself
        // to the primary lb
        msg.setTopic(MessageTopic.SERVERS_INFO);
        msg.setServersInfo(this.servers);
        this.sendMsgToLB(msg);

        this.rl.unlock();
    }

    public Message registerLoadBalancer(Message msg) {
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
        if (id == -1)
            return null;

        msg.setServerId(id);

        this.gui.registerLB(msg, primaryLB);

        this.rl.unlock();


        return msg;
    }

    public Message registerNewServer(int serverPort) {
        this.rl.lock();

        int id = this.serverCount++;

        ServerInfo serverInfo = new ServerInfo(id, serverPort, 0);

        this.servers.put(id, serverInfo);

        this.serverHeartbeatThreads.put(id, new HeartbeatManager(
                this.hostname, serverInfo.getServerPort(), id, true, this));
        this.serverHeartbeatThreads.get(id).start();

        Message msg = new Message(MessageTopic.SERVER_REGISTER, id, serverInfo.getServerPort());

        this.gui.registerServer(msg);

        this.rl.unlock();

        return msg;
    }

    public Message registerNewClient(Message msg) {
        this.rl.lock();

        int id = this.clientCount++;

        // store client info such as ports and put in the GUI
        this.clients.put(id, msg.getServerPort());

        msg.setClientId(id);
        msg.setTopic(MessageTopic.CLIENT_REGISTER_ACCEPTED);

        this.gui.registerClient(msg);

        this.rl.unlock();

        return msg;
    }

    public void serverDown(int serverId){
        this.rl.lock();

        servers.remove(serverId);
        // add to list the pending requests
        ArrayList<Message> pendingRequeststoRemove = new ArrayList<>();

        for (Message removemsg: this.pendingRequests.values()){
            if (removemsg.getServerId() == serverId) {
                Message reply = new Message(MessageTopic.SERVERS_INFO, removemsg.getRequestId(), removemsg.getServerId(), removemsg.getNI(), removemsg.getDeadline());
                reply.setServerPort(clients.get(removemsg.getClientId()));
                reply.setClientId(removemsg.getClientId());

                pendingRequeststoRemove.add(reply);
            }
        }

        serverHeartbeatThreads.remove(serverId);

        this.gui.removeServer(serverId);

        // add pending requests to message
        // send message to loadbalancer
        Message reply = new Message(MessageTopic.FORWARD_PENDING);
        reply.setPendingRequests(pendingRequeststoRemove);
        reply.setServersInfo(servers);

        sendMsgToLB(reply);

        this.rl.unlock();
    }

    public void sendMsgToLB(Message msg) {
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

        this.LBs.get(LBId).close();
        
        this.LBs.remove(LBId);

        if (LBId == primaryLB){
            primaryLB = -1;

            // send pending requests to other active lb
            if (! this.LBs.isEmpty()) {
                Integer activeLBId = (Integer) this.LBs.keySet().toArray()[0];
                if (activeLBId != null) {
                    primaryLB = activeLBId;
                    this.gui.turnPrimaryLB(primaryLB);
                    // send pending
                    Message msg = new Message();
                    msg.setTopic(MessageTopic.FORWARD_PENDING);
                    msg.setPendingRequests(new ArrayList<>(pendingRequests.values()));
                    // clear pending requests
                    pendingRequests.clear();
                    ClientAux LB = this.LBs.get(primaryLB);
                    if (LB != null) {
                        try {
                            LB.sendMsg(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        this.gui.removeLB(LBId);

        this.rl.unlock();
    }

    public void requestProcessed(Message msg){
        this.rl.lock();

        if (servers.containsKey(msg.getServerId())) {
            ServerInfo server = servers.get(msg.getServerId());
            server.setNI(server.getNI() - msg.getNI());
            server.setActiveReq(server.getActiveReq() - 1);

            this.gui.requestProcessed(msg.getRequestId(), server);
        }

        pendingRequests.remove(msg.getRequestId());

        this.rl.unlock();
    }

    public void requestInProcess(Message msg) {
        this.rl.lock();

        if (servers.containsKey(msg.getServerId())) {
            ServerInfo server = servers.get(msg.getServerId());
            server.setPendingReq(server.getPendingReq() - 1);
            server.setActiveReq(server.getActiveReq() + 1);

            this.gui.requestInProcess(msg.getRequestId(), server);
        }

        this.rl.unlock();
    }

    public void updateServerInfo(Message msg) {
        rl.lock();

        if (servers.containsKey(msg.getServerId())) {
            this.pendingRequests.put(msg.getRequestId(), msg);

            ServerInfo server = servers.get(msg.getServerId());
            server.setNI(server.getNI() + msg.getNI());
            server.setPendingReq(server.getPendingReq() + 1);
            this.gui.updateServerInfo(server, msg.getRequestId());
        }

        rl.unlock();
    }

    public void requestRejected(Message msg){
        this.rl.lock();

        if (pendingRequests.containsKey(msg.getRequestId()))
            pendingRequests.remove(msg.getRequestId());

        this.gui.requestRejected(msg);

        this.rl.unlock();
    }


    public Map<Integer,ServerInfo> getServersInfo(){
        return servers;
    }

    public static void main(String[] args) {
        new Monitor();
    }

}
