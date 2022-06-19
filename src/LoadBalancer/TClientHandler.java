package LoadBalancer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;
import Server.ServerInfo;

import static Communication.MessageTopic.HEARTBEAT_ACK;

public class TClientHandler extends Thread {

    private  final Socket clientSocket;
    private  final LoadBalancer lb;
    private ObjectInputStream in = null;
    private final String hostname = "localhost";
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
            Message msg;

            while (true) {
                try {
                    msg = (Message) in.readObject();
                    switch (msg.getTopic()){
                        case MessageTopic.LB_REGISTER:
                            this.lb.setLBId(msg.getServerId());
                            break;
                        case MessageTopic.CLIENT_REGISTER_PENDING:
                            this.lb.clientRegister(msg);
                            break;
                        case MessageTopic.CLIENT_REGISTER_ACCEPTED:
                            new ClientAux(hostname, msg.getServerPort(), msg).start();
                            break;
                        case MessageTopic.REQUEST:
                            this.lb.clientRequest(msg);
                            break;
                        case MessageTopic.SERVERS_INFO:
                            this.lb.forwardMessageToServer(msg);
                            break;
                        case MessageTopic.FORWARD_PENDING:
                            for(Message m: msg.getPendingRequests()){
                                System.out.println(msg.getServersInfo().size());
                                m.setServersInfo(msg.getServersInfo());
                                this.lb.forwardMessageToServer(m);
                            }
                            break;
                        default:
                            break;
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

    public void sendMsg(Message msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
