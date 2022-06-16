package Server;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import FIFO.IFIFO_Server;
import FIFO.MFIFO;

public class Server {
    private final ServerGUI gui;
    private final ServerAux server;
    private int port;
    private ClientAux monitorCon;
    private final int monitorPort = 9000;
    private final IFIFO_Server mFifo;
    private final String hostname = "localhost";
    private final int queueSize = 3;


    public Server() {
        this.server = new ServerAux();
        this.gui = new ServerGUI(this);
        this.mFifo = new MFIFO(queueSize);
        for (int i = 1; i <= queueSize; i++)
            new TComputeRequest(mFifo,this).start();
    }

    public void startServer() {
        this.monitorCon = new ClientAux(this.hostname, this.monitorPort);
        this.server.start();
    }

    public void registerInMonitor() {
        Message msg = new Message(MessageTopic.REGISTER_LB);
        this.monitorCon.sendMsg(msg);
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
        this.monitorCon.sendMsg(msg);
    }
    public void sendToClient(Message result, int port) {
        ClientAux socket = new ClientAux(this.hostname, port);
        socket.sendMsg(result);
    }

    public static void main(String[] args) {
        new Server();        
    }
}
