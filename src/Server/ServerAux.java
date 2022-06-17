package Server;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;

import java.net.ServerSocket;
import java.net.Socket;


public class ServerAux extends Thread  {
    private final String hostname;
    private int port;
    private final int monitorPort;
    private final Server server;
    private ServerSocket serverSocket;


    public ServerAux(Server server, String hostname, int monitorPort) {
        this.server = server;
        this.hostname = hostname;
        this.monitorPort = monitorPort;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(0);
            this.port = serverSocket.getLocalPort();
            this.server.getGui().setServerPort(this.port);

            this.server.setMonitorCon(new ClientAux(
                    this.hostname, this.monitorPort, new Message(MessageTopic.SERVER_REGISTER, this.port)));
            this.server.getMonitorCon().start();

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
                // This thread will handle the client separately
                clientSock.start();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}