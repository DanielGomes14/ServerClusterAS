package Server;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import FIFO.IFIFO_Server;
import FIFO.MFIFO;

public class Server {
    private final ServerGUI gui;
    private final ServerAux serverAux;
    private int port;
    private final ClientAux monitorCon;
    private final int monitorPort = 5000;
    private final IFIFO_Server mFifo;
    private final String hostname = "localhost";
    private final int queueSize = 3;


    public Server() {
        this.serverAux = new ServerAux(this);
        this.mFifo = new MFIFO(queueSize);
        for (int i = 1; i <= queueSize; i++)
            new TComputeRequest(mFifo,this).start();
        this.gui = new ServerGUI(this);
        this.monitorCon = new ClientAux(this.hostname, this.monitorPort, new Message(MessageTopic.SERVER_REGISTER));
    }

    public ServerGUI getGui() {
        return this.gui;
    }

    public void start() {
        new Thread(this.serverAux).start();
        
        this.monitorCon.start();

    }

    public void registerInMonitor() {
        Message msg = new Message(MessageTopic.SERVER_REGISTER);
        this.monitorCon.sendMsg(msg);
    }

    public void processRequest(Message msg) {
        //TODO: Check Number of iterations that the server may process
        if(this.mFifo.isFull()){
            msg.setTopic(MessageTopic.REJECTION);
            this.monitorCon.sendMsg(msg);
            // reply with that the request cannot be processed at the momment
        }
        this.mFifo.put(msg);
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

    public void end() {
        Message msg = new Message();
        msg.setTopic(MessageTopic.REMOVE_SERVER);
        this.monitorCon.sendMsg(msg);
        this.serverAux.close();
    }


    public static void main(String[] args) {
        new Server();        
    }
}
