package Server;

import Communication.Message;

import java.io.ObjectInputStream;
import java.net.Socket;

public class TClientHandler implements Runnable {

    private  final Socket clientSocket;
    private ObjectInputStream in = null;
    private final Server server;

    public TClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }


    @Override
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
