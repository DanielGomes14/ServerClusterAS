package LoadBalancer;

import java.util.HashMap;
import java.util.Map;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import Server.ServerInfo;

public class LoadBalancer {
    
    private ClientAux monitorCon;
    private Map<Integer, ServerInfo> servers;
    private final ServerAux serverAux;
    private final String hostname = "localhost";
    private final int port = 5000;
    private final int monitorPort = 8888;
    private final LoadBalancerGUI gui;
    private Map<Integer, Message> pendingRequests;
    
    public LoadBalancer() {
        this.gui = new LoadBalancerGUI(this);
        this.servers = new HashMap<>();
        this.pendingRequests = new HashMap<>();
        this.serverAux = new ServerAux(this);
    }

    public void start(int port) {
        this.serverAux.setPort(port);
        new Thread(this.serverAux).start();
        this.registerInMonitor();
    }

    public void end() {
        this.serverAux.close();
    }
   

    public void registerInMonitor() {
        this.monitorCon = new ClientAux(hostname, monitorPort);
        // send msg to Monitor informing that the LB Is running
        Message msg = new Message(MessageTopic.REGISTER_LB);
        this.monitorCon.sendMsg(msg);
    }

    public void clientRegister(Message msg) {
        this.monitorCon.sendMsg(msg);
    }

    public void clientRequest(Message msg) {
        this.monitorCon.sendMsg(msg);
        pendingRequests.put(msg.getRequestId(), msg);
    }

    public Message getHighestPriorityRequest() {
        int earliestDeadline = Integer.MAX_VALUE;
        Message highestPriorityRequest = null;

        for (Message request : pendingRequests.values()) {
            if (earliestDeadline < request.getDeadline()) {
                highestPriorityRequest = request;
            }
        }

        return highestPriorityRequest;
    }

    public ServerInfo chooseBestServer(Message msg) {
        int minNI = Integer.MAX_VALUE;
        ServerInfo bestServer = null;
        
        for (ServerInfo server : msg.getServersInfo().values()) {
            if (minNI < server.getNI()) {
                bestServer = server;
            }
        }

		return bestServer;
	}

    public void sendServerRequest(Message msg) {
        ClientAux socket = new ClientAux(hostname, msg.getServerPort());
        socket.sendMsg(msg);
    }

    public void forwardPendingRequests(Message msg) {
        // for (Message msg: msg.getPendingRequests())
        //     forwardMessageToServer(msg);
    }

    public void forwardMessageToServer(Message msg) {
        // choose best server
        ServerInfo bestServer = chooseBestServer(msg);
        if (bestServer == null) {
            // send to client rejected status
            // send to monitor rejected status
            return;
        }
        Message request = getHighestPriorityRequest();
        if (request == null) {
            // shouldnt happen but ok
            return;
        }
        // send msg with correct information
        request.setServerId(bestServer.getServerId());
        request.setServerPort(bestServer.getServerPort());
        sendServerRequest(request);
    }

    public static void main(String args[]) {
        new LoadBalancer();
    }
}
