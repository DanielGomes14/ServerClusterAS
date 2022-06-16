package LoadBalancer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Communication.Message;
import Communication.MessageTopic;

import static Communication.MessageTopic.HEARTBEAT_ACK;

public class TClientHandler implements Runnable{

    private  final Socket clientSocket;
    private  final LoadBalancer lb;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public TClientHandler(Socket socket, LoadBalancer lb) {
        this.clientSocket = socket;
        this.lb = lb;
    }

    @Override
    public void run() {
        try {
            // get the input stream of client
            in =  new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            Message message;

            while (true) {
                try {
                    message = (Message) in.readObject();
                    switch ((message.getTopic())){
                        case MessageTopic.HEARTBEAT:
                            Message m = new Message(HEARTBEAT_ACK);
                            sendMsg(m);
                            break;
                        case MessageTopic.REQUEST:
                            this.lb.clientRequest(message);

                    }
                    // client requests
                    // monitor heartbeat
                    // monitor forward requests to servers

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

    public void sendMsg(Message msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
