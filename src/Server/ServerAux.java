package Server;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerAux extends Thread  {
    private final String hostname;
    private int port;
    private final int monitorPort;
    private final Server server;
    private ServerSocket serverSocket;
    private List<TClientHandler> activeConnections;


    public ServerAux(Server server, String hostname, int monitorPort) {
        this.server = server;
        this.hostname = hostname;
        this.monitorPort = monitorPort;
        this.activeConnections = new ArrayList<>();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(0);
            this.port = serverSocket.getLocalPort();
            this.server.getGui().setServerPort(this.port);

            new ClientAux(
                    this.hostname, this.monitorPort, new Message(MessageTopic.SERVER_REGISTER, this.port),true).start();

            // running infinite loop for getting
            // client request
            while (true) {
                // socket object to receive incoming client
                // requests
                Socket client = serverSocket.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected");

                // create a new thread object
                TClientHandler clientSock = new TClientHandler(client, server);

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