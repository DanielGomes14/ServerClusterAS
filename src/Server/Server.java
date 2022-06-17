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
    private int port;
    private  ClientAux monitorCon;
    private final int monitorPort = 5000;
    private final IFIFO_Server mFifo;
    private final String hostname = "localhost";
    private final int queueSize = 3;


    public Server() {
        this.mFifo = new MFIFO(queueSize);
        for (int i = 1; i <= queueSize; i++)
            new TComputeRequest(mFifo,this).start();
        this.gui = new ServerGUI(this);
    }

    public void start() {
        this.serverAux = new ServerAux(this, this.hostname, this.monitorPort);
        this.serverAux.start();
    }

    public ClientAux getMonitorCon() { return this.monitorCon; };

    public void setMonitorCon(ClientAux monitorCon) { this.monitorCon = monitorCon; };

    public ServerGUI getGui() {
        return this.gui;
    }

    public void processRequest() {
        //TODO: Check Number of iterations that the server may process
        if(this.mFifo.isFull()){
            // reply with that the request cannot be processed at the momment
        }
        this.mFifo.put(null);

        // process com time to sleeps ig

        // send to client processed result
        // sendToClient(null, 0);

        // send to monitor request finished processing
        // this.monitorCon.sendMsg(null);

        // this.mFifo.get();
    }

    public  void sendtoMonitor(Message msg){
        try {
            this.monitorCon.sendMsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToClient(Message result, int port) {
        ClientAux socket = new ClientAux(this.hostname, port);
        try {
            socket.sendMsg(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void end() {
        this.serverAux.close();
    }


    public static void main(String[] args) {
        new Server();        
    }
}
