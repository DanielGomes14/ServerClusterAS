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
    private final int monitorPort = 5000;
    private IFIFO_Server mFifo;
    private final String hostname = "localhost";
    private final int queueSize = 5;
    private final int numWorkers = 3;
    private TComputeRequest [] activeThreads;

    public Server() {
        this.gui = new ServerGUI(this);
    }

    public void start()  {
        this.mFifo = new MFIFO(queueSize);
        this.activeThreads = new TComputeRequest[numWorkers];
        for (int i = 0; i < numWorkers; i++){
            this.activeThreads[i] = new TComputeRequest(mFifo,this);
            this.activeThreads[i].start();
        }
        this.serverAux = new ServerAux(this, this.hostname, this.monitorPort);
        this.serverAux.start();
    }


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
            this.gui.requestRejected(msg);
            // Inform Monitor that the Request has been rejected
            this.sendtoMonitor(msg);

            // inform also the Client
            this.sendToClient(msg, msg.getServerPort());
        } else {
            this.mFifo.put(msg);
            this.gui.addPendingRequest(msg);
        }
    }

    public  void sendtoMonitor(Message msg){
        new ClientAux(
                this.hostname, this.monitorPort, msg,true).start();
    }

    public void sendToClient(Message result, int port) {
        new ClientAux(this.hostname, port, result,true).start();
    }

    public void killThreads(){
        for(int i=0; i < numWorkers; i++){
            this.activeThreads[i].setEnd(true);
            this.activeThreads[i].interrupt();
            System.out.println("Thread interrupted");
        }
    }
    public void end() {
        this.serverAux.close();
        this.killThreads();
        this.gui.end();
    }

    public int getServerId() {
        return serverId;
    }

    public static void main(String[] args) {
        new Server();
    }
}
