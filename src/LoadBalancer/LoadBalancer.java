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
            if (minNI > server.getNI() && server.getActiveReq() + server.getPendingReq() < 5 && server.getNI() + msg.getNI() > 20) {
                bestServer = server;
            }
        }

		return bestServer;
	}

    public void sendServerRequest(Message msg, int port) {
        ClientAux socket = new ClientAux(hostname, port, msg);
        socket.start();
        //TODO: Close Connection
    }

    public void forwardPendingRequests(Message msg) {
        // for (Message msg: msg.getPendingRequests())
        //     forwardMessageToServer(msg);
    }

    public void forwardMessageToServer(Message msg) {
        // choose best server
        ServerInfo bestServer = chooseBestServer(msg);
        if (bestServer == null) {
            // send to monitor rejected status
            msg.setTopic(MessageTopic.REJECTION);
            msg.setServerId(-1);
            this.gui.setServerIdRequest(msg);
            // send to client rejected status
            sendServerRequest(msg, msg.getServerPort());
            return;
        }

        msg.setServerId(bestServer.getServerId());
        sendServerRequest(msg, bestServer.getServerPort());
        this.gui.setServerIdRequest(msg);
    
        msg.setTopic(MessageTopic.REQUEST_ACK);

        try {
            this.monitorCon.sendMsg(msg);
        }        
        catch(IOException e){
            //
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
