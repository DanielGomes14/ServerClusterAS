package Server;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import FIFO.IFIFO_Server;
import FIFO.MFIFO;

import java.io.IOException;

public class Server {
    private final ServerGUI gui;
    private ServerAux serverAux;
    private int serverId;
    private  ClientAux monitorCon;
    private final int monitorPort = 5000;
    private final IFIFO_Server mFifo;
    private final String hostname = "localhost";
    private final int queueSize = 5;
    private final int numWorkers = 3;


    public Server() {
        this.mFifo = new MFIFO(queueSize);
        for (int i = 1; i <= numWorkers; i++)
            new TComputeRequest(mFifo,this).start();
        this.gui = new ServerGUI(this);
    }

    public void start() {
        this.serverAux = new ServerAux(this, this.hostname, this.monitorPort);
        this.serverAux.start();
    }

    public ClientAux getMonitorCon() { return this.monitorCon; }

    public void setMonitorCon(ClientAux monitorCon) { this.monitorCon = monitorCon; }

    public ServerGUI getGui() {
        return this.gui;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
        this.gui.setServerId(serverId);
    }

    public void processRequest(Message msg) {
        if (this.mFifo.isFull() || !this.mFifo.checkNICounter(msg.getNI())) {
            msg.setTopic(MessageTopic.REJECTION);

            try{
                this.gui.requestRejected(msg);
                // Inform Monitor that the Request has been rejected
                this.monitorCon.sendMsg(msg);
                // inform also the Client
                this.sendToClient(msg, msg.getServerPort());
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }
        this.mFifo.put(msg);
        this.gui.addPendingRequest(msg);
    }

    public  void sendtoMonitor(Message msg){
        try {
            this.monitorCon.sendMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToClient(Message result, int port) {
        new ClientAux(this.hostname, port, result).start();
    }

    public void end() {
        this.serverAux.close();
    }

    public int getServerId() {
        return serverId;
    }

    public static void main(String[] args) {
        new Server();
    }
}
