package Server;

import java.net.ServerSocket;
import java.net.Socket;


public class ServerAux extends Thread  {

    private int port;
    private final Server server;
    private ServerSocket serverSocket;


    public ServerAux(Server server) {
        this.server = server;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(0);
            this.port = serverSocket.getLocalPort();
            this.server.getGui().setServerPort(port);

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