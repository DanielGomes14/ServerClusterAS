package LoadBalancer;

import java.net.ServerSocket;
import java.net.Socket;


public class ServerAux extends Thread {

    private final int port;
    private ServerSocket serverSocket;
    private final LoadBalancer lb;


    public ServerAux(LoadBalancer lb, int port) {
        this.lb = lb;
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
