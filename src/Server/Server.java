package Server;

import Communication.ClientAux;
import Communication.Message;
import FIFO.MFIFO;

public class Server {
    private final ServerGUI gui;
    private final ServerAux server;
    private int port;
    private ClientAux monitorCon;
    private final int monitorPort = 9000;
    private final MFIFO mFifo;
    private final String hostname = "localhost";
    private final int queueSize = 3;


    public Server() {
        this.server = new ServerAux();
        this.gui = new ServerGUI(this);
        this.mFifo = new MFIFO(queueSize);
    }

    public void startServer() {
        this.monitorCon = new ClientAux(this.hostname, this.monitorPort);
        this.server.start();
    }

    public void registerInMonitor() {
        Message msg = new Message();
        this.monitorCon.sendMsg(msg);
    }

    public void processRequest() {
        this.mFifo.put(null);

        // process com time to sleeps ig

        // send to client processed result
        sendToClient(null, 0);

        // send to monitor request finished processing
        this.monitorCon.sendMsg(null);

        this.mFifo.get();
    }
    
    public void sendToClient(String result, int port) {
        ClientAux socket = new ClientAux(this.hostname, port);
        socket.sendMsg(null);
    }

    public static void main(String[] args) {
        new Server();        
    }
}
