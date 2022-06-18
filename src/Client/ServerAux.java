package Client;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerAux extends Thread {
    private final String hostname;
    private int port;
    private final int LBPort;
    private final Client client;
    private ServerSocket serverSocket;
    private List<TClientHandler> activeConnections;
    
    public ServerAux(Client client, String hostname, int LBPort) {
        this.client = client;
        this.hostname = hostname;
        this.activeConnections = new ArrayList<>();
        this.LBPort = LBPort;
    }

    public int getPort() {
        return port;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(0);
            this.port = serverSocket.getLocalPort();
            this.client.getGui().setClientPort(port);

            this.client.setClientAux(new ClientAux(this.hostname, this.LBPort,
                    new Message(MessageTopic.CLIENT_REGISTER_PENDING, this.port)));
            this.client.getClientAux().start();

            // running infinite loop for getting client requests
            while (true) {
                // socket object to receive incoming client requests
                Socket socketClient = serverSocket.accept();

                // Displaying that new client is connected to server
                System.out.println("New client connected");

                // create a new thread object
                TClientHandler clientSock = new TClientHandler(socketClient, client);

                // This thread will handle the client
                // separately
                Thread clientThread = new Thread(clientSock);
                clientThread.start();

                activeConnections.add(clientSock);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void close() {
        try {
            for (TClientHandler clientSock: activeConnections) {
                clientSock.stopServer();
            }
            serverSocket.close();
            activeConnections = new ArrayList<>();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}