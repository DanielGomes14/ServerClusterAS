package Client;

import java.net.ServerSocket;
import java.net.Socket;


public class ServerAux extends Thread {
    private int port;
    private final Client client;
    private ServerSocket serverSocket;

    public ServerAux(Client client) {
        this.client = client;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(0);
            this.port = serverSocket.getLocalPort();
            this.client.getGui().setClientPort(port);

            // running infinite loop for getting client requests
            while (true) {
                // socket object to receive incoming client requests
                Socket socketClient = serverSocket.accept();

                // Displaying that new client is connected to server
                System.out.println("New client connected");

                // create a new thread object
                TClientHandler clientSock = new TClientHandler(socketClient, client);

                // This thread will handle the client separately
                new Thread(clientSock).start();
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