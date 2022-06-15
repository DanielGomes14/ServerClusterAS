package Client;

import java.awt.*;

public class Client {
    private final ClientGUI gui;
    private final EventQueue queue;
    private final ServerAux server;
    
    public Client() {
        this.queue = new EventQueue();
        this.server = new ServerAux();
        this.gui = new ClientGUI(this);
    }


    public static void main(String[] args) {
        new Client();        
    }
}
