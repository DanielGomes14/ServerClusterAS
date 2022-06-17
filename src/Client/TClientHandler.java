package Client;

import java.io.ObjectInputStream;
import java.net.Socket;

import Communication.Message;

public class TClientHandler extends Thread {

    private  final Socket clientSocket;
    private ObjectInputStream in = null;
    private final Client client;

    public TClientHandler(Socket socket, Client client) {
        this.clientSocket = socket;
        this.client = client;
    }


    public void run() {
        try {
            // get the input stream of client
            in =  new ObjectInputStream(clientSocket.getInputStream());
            Message message;

            while (true) {
                try {
                    message = (Message) in.readObject();



                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
