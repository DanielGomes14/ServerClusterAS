package Client;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;

import java.awt.*;

public class Client {
    private final EventQueue queue;
    private final ClientGUI gui;
    private final String hostname = "localhost";
    private ServerAux serverAux;
    private ClientAux clientAux;

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
    }

    public ClientGUI getGui() {
        return this.gui;
    }

    public ClientAux getClientAux() { return this.clientAux; };

    public void setClientAux(ClientAux clientAux) { this.clientAux = clientAux; };


    public static void main(String[] args) {
        new Client();
    }
}
