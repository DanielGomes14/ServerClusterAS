package Client;

import ClientGUI.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Client {
    private final ClientGUI gui;
    private final EventQueue queue;
    private final ServerAux server;
    
    public Client() {
        this.queue = new EventQueue();
        try {
            this.server = new ServerAux();
            this.server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.gui = new ClientGUI(this);
    }


    public static void main(String args[]) {
        new Client();        
    }
}
