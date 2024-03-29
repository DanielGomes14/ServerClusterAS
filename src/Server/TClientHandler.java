package Server;

import Communication.Message;
import Communication.MessageTopic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TClientHandler extends Thread {

    private  final Socket clientSocket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
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
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            Message msg;

            while (true) {
                try {
                    msg = (Message) in.readObject();
                    switch ((msg.getTopic())) {
                        case MessageTopic.REQUEST:
                            this.server.processRequest(msg);
                            break;
                        case MessageTopic.SERVER_REGISTER:
                            this.server.setServerId(msg.getServerId());
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        try {
            if (in != null) {
                in.close();
                clientSocket.close();
            }
            if (out != null)
                out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
