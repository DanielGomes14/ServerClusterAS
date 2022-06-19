package LoadBalancer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import Server.ServerInfo;

public class LoadBalancer {
    
    private ServerAux serverAux;
    private final int MAX_SERVER_REQUESTS = 5;
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
        this.sendtoMonitor(new Message(MessageTopic.LB_REGISTER, this.port));
    }

    public void end() {
        this.gui.clearInt();
        this.serverAux.close();
        this.serverAux = null;
    }
   

    public void clientRegister(Message msg) {
        this.sendtoMonitor(msg);
    }

    public void clientRequest(Message msg) {
        this.gui.addPendingRequest(msg);
        this.sendtoMonitor(msg);
    }

    public ServerInfo chooseBestServer(Message msg) {
        int minNI = Integer.MAX_VALUE;
        ServerInfo bestServer = null;

        // get the server with the lowest NI and
        for (ServerInfo server : msg.getServersInfo().values()) {
            System.out.println(String.format("Server Id: %d", server.getServerId()));
            System.out.println(String.format("soma: %d", server.getActiveReq() + server.getPendingReq()));
            System.out.println(String.format("ACTIVe: %d", server.getActiveReq()));
            System.out.println(String.format("getPendingReq: %d", server.getPendingReq()));
            System.out.println(minNI > server.getNI() &&
                    ( server.getActiveReq() + server.getPendingReq() ) < MAX_SERVER_REQUESTS &&
                    ( server.getNI() + msg.getNI() ) <= MAX_SERVER_NI);

            if (
                    minNI > server.getNI() &&
                    ( server.getActiveReq() + server.getPendingReq() ) < MAX_SERVER_REQUESTS &&
                    ( server.getNI() + msg.getNI() ) <= MAX_SERVER_NI
            ) {
                server.setNI(server.getNI() + msg.getNI());
                server.setPendingReq(server.getPendingReq() + 1);

                msg.getServersInfo().put(server.getServerId(), server);
                msg.setServersInfo(msg.getServersInfo());

                bestServer = server;
                minNI = server.getNI();
            }
        }
        System.out.println(bestServer);

		return bestServer;
	}

    public  void sendtoMonitor(Message msg){
        // start the connection and register in the monitor
        new ClientAux(hostname, monitorPort, msg, true).start();
    }

    public void sendServerRequest(Message msg, int port) {
        new ClientAux(hostname, port, msg,true).start();
    }


    public void forwardMessageToServer(Message msg) {
        // choose best server
        ServerInfo bestServer = chooseBestServer(msg);

        if (bestServer == null) {
            msg.setTopic(MessageTopic.REJECTION);
            // send to monitor rejected status
            this.sendtoMonitor(msg);

            int clientId = msg.getClientId();
            Message reply = new Message(MessageTopic.REJECTION,msg.getRequestId(),clientId, msg.getNI(), msg.getDeadline());
            reply.setClientId(clientId);
            reply.setServerPort(msg.getServerPort());
            System.out.println("AQUISADIQIWE");

            this.gui.setServerIdRequest(reply.getRequestId(), clientId, -1, reply.getNI(), reply.getDeadline());
            // send to client rejected status
            //TODO: Server Port Wrong:  sending to the server instead of client!
            sendServerRequest(reply, reply.getServerPort());
            return;
        }

        Message msgToMonitor = new Message(MessageTopic.REQUEST_ACK, bestServer.getServerId(), bestServer.getServerPort());
        msgToMonitor.setClientId(msg.getClientId());
        msgToMonitor.setRequestId(msg.getRequestId());
        msgToMonitor.setNI(msg.getNI());
        msgToMonitor.setDeadline(msg.getDeadline());

        this.sendtoMonitor(msgToMonitor);

        msg.setTopic(MessageTopic.REQUEST);
        sendServerRequest(msg, bestServer.getServerPort());

        this.gui.setServerIdRequest(msg.getRequestId(), msg.getClientId(), bestServer.getServerId(), msg.getNI(), msg.getDeadline());

    }

    public void setLBId(int LBId) {
        this.LBId = LBId;
        this.gui.setLBId(LBId);
    }

    public static void main(String args[]) {
        new LoadBalancer();
    }
}
