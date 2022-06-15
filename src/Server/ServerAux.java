package Server;

import Client.TClientHandler;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerAux {

    private int port;
    private InetAddress addr;
    private ServerSocket serverSocket;


    public ServerAux() {
    }

    public void start() {
        try {
            serverSocket = new ServerSocket();
            this.port = serverSocket.getLocalPort();
            this.addr = serverSocket.getInetAddress();

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
                TClientHandler clientSock = new TClientHandler(client);

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