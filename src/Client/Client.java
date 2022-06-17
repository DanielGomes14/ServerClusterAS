package Client;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;

import java.awt.*;

public class Client {
    private final EventQueue queue;
    private final ClientGUI gui;
    private final String hostname = "locahost";
    private final ServerAux serverAux;
    private ClientAux client;

    public Client() {
        this.queue = new EventQueue();
        this.serverAux = new ServerAux(this);
        this.gui = new ClientGUI(this);
        // connect to LB on the given
    }

    public ClientGUI getGui() {
        return this.gui;
    }

    public void start(int port) {
        serverAux.start();

        this.client = new ClientAux(hostname, port, new Message(MessageTopic.CLIENT_REGISTER));
        // connect to LB on the given
        client.start();
    }

    public void end() {
        this.serverAux.close();
    }

    public static void main(String[] args) {
        new Client();
    }
}
