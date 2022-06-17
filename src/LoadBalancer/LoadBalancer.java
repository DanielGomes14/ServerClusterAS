package LoadBalancer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import Server.ServerInfo;

public class LoadBalancer {
    
    private ClientAux monitorCon;
    private Map<Integer, ServerInfo> servers;
    private ServerAux serverAux;
    private final String hostname = "localhost";
    private int port;
    private final int monitorPort = 5000;
    private final LoadBalancerGUI gui;
    private Map<Integer, Message> pendingRequests;
    
    public LoadBalancer() {
        this.gui = new LoadBalancerGUI(this);
        this.servers = new HashMap<>();
        this.pendingRequests = new HashMap<>();
    }

    public void start(int port) {
        this.port = port;

        // start my server
        this.serverAux = new ServerAux(this, port);
        this.serverAux.start();

        // start the connection and register in the monitor
        this.monitorCon = new ClientAux(hostname, monitorPort, new Message(MessageTopic.LB_REGISTER, this.port));
        this.monitorCon.start();
    }

    public void end() {
        this.serverAux.close();
    }
   

    public void clientRegister(Message msg) {
        try {
            this.monitorCon.sendMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientRequest(Message msg) {
        try {
            this.monitorCon.sendMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ClientAux socket = new ClientAux(hostname, msg.getServerPort(), msg);
        socket.start();
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
