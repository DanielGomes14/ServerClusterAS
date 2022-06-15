package Server;

import ServerGUI.ServerGUI;

import javax.swing.*;

import Client.ServerAux;
import Communication.Message;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Server {
    private final ClientGUI gui;
    private final EventQueue queue;
    private final ServerAux server;
    private final String hostname;
    private final int port;


    public Server() {
        this.queue = new EventQueue();
        this.hostname = hostname;
        this.port = port;
        try {
            this.server = new ServerAux();
            this.server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.gui = new Server(this);
        this.monitorConnection = new ClientAux(serverHostName, serverPort)
    }

    public void registerInMonitor() {
        Message msg = new Message();
        this.monitorConnection.sendMsg(msg)
        
    }

    public void processRequest() {
        queue.put();

        // process com time to sleeps ig

        // send to client aqui?
        sendToClient()

        // send to monitor request finished processing
        this.monitorConnection.sendMsg()

        queue.get()
    }
    
    public void sendToClient(String result, int port) {
        ClientAux socket = new ClientAux(port);
        socket.sendMsg();
    }

    public static void main(String args[]) {
        new Server();        
    }
}
