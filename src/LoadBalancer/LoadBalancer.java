package LoadBalancer;

import java.util.HashMap;
import java.util.Map;

import Communication.ClientAux;
import Communication.Message;
import Server.ServerInfo;

public class LoadBalancer {
    
    private ClientAux monitorConn;
    private Map<Integer,ServerInfo> servers;
    private final ServerAux serverAux;
    private final String hostname = "localhost";
    private String port;
    private final LoadBalancerGUI gui;
    
    public LoadBalancer() {
        this.gui = new LoadBalancerGUI(this);
        this.servers = new HashMap<Integer, ServerInfo>();
        this.serverAux = new ServerAux(this);
    }

    public void registerInMonitor() {
        // send msg to Monitor informing that the LB Is running
        Message msg = new Message();
        this.monitorConn.sendMsg(msg);
    }

    public void clientRequest(Message msg) {
        this.monitorConn.sendMsg(msg);
    }

    public void sendServerRequest(Message msg) {
//        ClientAux socket = new ClientAux(hostname, msg.getServerPort());
//        socket.sendMsg(msg);
    }


    public void forwardPendingRequests() {
        
    }

    public static void main(String args[]) {
        new LoadBalancer();
    }
}
