package Client;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;

import java.awt.*;
import java.io.IOException;

public class Client {
    private final EventQueue queue;
    private final ClientGUI gui;
    private final String hostname = "localhost";
    private ServerAux serverAux;
    private int clientId;
    private int nRequests = 0;

    public Client() {
        this.queue = new EventQueue();
        this.gui = new ClientGUI(this);
    }

    public void start(int LBPort) {
        // start my server and
        // connect to LB on the given port and register clientAux
        this.serverAux = new ServerAux(this, hostname, LBPort);
        this.serverAux.start();
    }

    public void end() {
        this.serverAux.close();
        this.gui.end();
    }

    public ClientGUI getGui() {
        return this.gui;
    }

    public  void sendToLB(Message msg){
        new ClientAux(this.hostname, this.serverAux.getLBPort(), msg, true).start();
    }

    public void sendRequest(int nRequests, int NI, int deadline) throws IOException {
        for (int i=0; i<nRequests; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message request = new Message(
                    MessageTopic.REQUEST,
                    1000*clientId+(this.nRequests++),
                    this.clientId,
                    NI,
                    deadline);
            request.setClientId(this.clientId);
            this.sendToLB(request);
            this.gui.addPendingRequest(request);
        }
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
        this.gui.setClientId(clientId);
    }


    public static void main(String[] args) {
        new Client();
    }
}
