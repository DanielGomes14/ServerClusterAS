package LoadBalancer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import Server.ServerInfo;

public class LoadBalancer {
    
    private ClientAux monitorCon;
    private ServerAux serverAux;
    private final int MAX_SERVER_REQUESTS= 5;
    private final int MAX_SERVER_NI= 20;
    private final String hostname = "localhost";
    private int port;
    private final int monitorPort = 5000;
    private final LoadBalancerGUI gui;
    private int LBId;

    public LoadBalancer() {
        this.gui = new LoadBalancerGUI(this);
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
        this.serverAux = null;
        this.monitorCon = null;
    }
   

    public void clientRegister(Message msg) {
        try {
            this.monitorCon.sendMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientRequest(Message msg) {
        this.gui.addPendingRequest(msg);
        try {
            this.monitorCon.sendMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerInfo chooseBestServer(Message msg) {
        int minNI = Integer.MAX_VALUE;
        ServerInfo bestServer = null;

        // get the server with the lowest NI and
        for (ServerInfo server : msg.getServersInfo().values()) {
            if (
                    minNI > server.getNI() &&
                    ( server.getActiveReq() + server.getPendingReq() ) <= MAX_SERVER_REQUESTS
                    && server.getNI() + msg.getNI() < MAX_SERVER_NI
            ) {
                bestServer = server;
            }
        }

		return bestServer;
	}


    public void sendServerRequest(Message msg, int port) {
        ClientAux socket = new ClientAux(hostname, port, msg);
        socket.start();
    }


    public void forwardMessageToServer(Message msg) {
        // choose best server
        ServerInfo bestServer = chooseBestServer(msg);

        if (bestServer == null) {
            // send to monitor rejected status
            msg.setTopic(MessageTopic.REJECTION);
            try {
                this.monitorCon.sendMsg(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            msg.setServerId(-1);
            this.gui.setServerIdRequest(msg.getRequestId(), bestServer.getServerId());
            // send to client rejected status
            sendServerRequest(msg, msg.getServerPort());
            return;
        }

        System.out.println(bestServer.getServerPort());

        msg.setTopic(MessageTopic.REQUEST);
        sendServerRequest(msg, bestServer.getServerPort());

        this.gui.setServerIdRequest(msg.getRequestId(), bestServer.getServerId());

        Message msgToMonitor = new Message(MessageTopic.REQUEST_ACK, bestServer.getServerId(), bestServer.getServerPort());
        msgToMonitor.setRequestId(msg.getRequestId());

        try {
            this.monitorCon.sendMsg(msgToMonitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLBId(int LBId) {
        this.LBId = LBId;
        this.gui.setLBId(LBId);
    }

    public static void main(String args[]) {
        new LoadBalancer();
    }
}
