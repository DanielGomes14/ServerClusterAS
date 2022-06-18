package Communication;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientAux extends Thread {
    private final String serverHostName;
    private int serverPort;

    private Socket clientSocket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private final Message firstMessage;

    public ClientAux(String serverHostName, int serverPort) {
        this.serverHostName = serverHostName;
        this.serverPort = serverPort;
        this.firstMessage = null;
    }

    public ClientAux(String serverHostName, int serverPort, Message firstMessage) {
        this.serverHostName = serverHostName;
        this.serverPort = serverPort;
        this.firstMessage = firstMessage;
    }

    public void run() {
        this.startConnection();
    }

    public void startConnection() {
        try {
            clientSocket = new Socket(serverHostName, serverPort);

            // get the output stream of the client
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            // get the input stream of the client
            in = new ObjectInputStream(clientSocket.getInputStream());

            if (firstMessage != null)
                sendMsg(firstMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message msg) throws IOException {
        out.writeObject(msg);
    }

    public void close() {
        try {
            if (in != null) {
                    in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
