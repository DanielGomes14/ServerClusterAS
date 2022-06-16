package Client;

import java.awt.*;

public class Client {
    private final EventQueue queue;
    private final ClientGUI gui;
    private final String hostname = "locahost";
    private final ServerAux serverAux;

    public Client() {
        this.queue = new EventQueue();
        this.serverAux = new ServerAux(this);
        this.gui = new ClientGUI(this);
    }

    public ClientGUI getGui() {
        return this.gui;
    }

    public void start(int port) {
        new Thread(this.serverAux).start();
        // connect to LB on the given port
    }

    public void end() {
        this.serverAux.close();
    }

    public static void main(String[] args) {
        new Client();
    }
}
