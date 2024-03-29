package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Communication.Message;
import Communication.MessageTopic;

public class TClientHandler extends Thread {

    private  final Socket clientSocket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private final Client client;

    public TClientHandler(Socket socket, Client client) {
        this.clientSocket = socket;
        this.client = client;
    }

    public void run() {
        try {
            // get the input stream of client
            in =  new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            Message msg;

            while (true) {
                try {
                    msg = (Message) in.readObject();

                    switch (msg.getTopic()) {
                        case MessageTopic.CLIENT_REGISTER_ACCEPTED:
                            this.client.setClientId(msg.getClientId());
                            break;
                        case MessageTopic.REPLY:
                            this.client.getGui().addReply(msg);
                            break;
                        case MessageTopic.REJECTION:
                            this.client.getGui().requestRejected(msg);
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
