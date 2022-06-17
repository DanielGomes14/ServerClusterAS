package Monitor;

import java.net.ServerSocket;
import java.net.Socket;


public class ServerAux extends Thread {

    private final int port;
    private ServerSocket serverSocket;
    private final Monitor monitor;


    public ServerAux(Monitor monitor, int port) {
        this.monitor = monitor;
        this.port = port;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);

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
                TClientHandler clientSock = new TClientHandler(client, monitor);

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
