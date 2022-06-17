package LoadBalancer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerAux extends Thread {

    private final int port;
    private ServerSocket serverSocket;
    private final LoadBalancer lb;
    private List<TClientHandler> activeConnections;


    public ServerAux(LoadBalancer lb, int port) {
        this.lb = lb;
        this.activeConnections = new ArrayList<>();
        this.port = port;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);

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
                TClientHandler clientSock = new TClientHandler(client, lb);

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
