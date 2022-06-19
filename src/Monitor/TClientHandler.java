package Monitor;

import Communication.ClientAux;
import Communication.Message;
import Communication.MessageTopic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TClientHandler extends Thread {

    private final Socket clientSocket;
    private final Monitor monitor;
    private final String hostname = "localhost";
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public TClientHandler(Socket socket, Monitor monitor) {
        this.clientSocket = socket;
        this.monitor = monitor;
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
                    switch ((msg.getTopic())) {
                        case MessageTopic.REQUEST:
                            // receive request from a client
                            this.monitor.receiveNewRequest(msg);
                            break;
                        case MessageTopic.LB_REGISTER:
                            new ClientAux(
                                    hostname,
                                    msg.getServerPort(),
                                    this.monitor.registerLoadBalancer(msg)
                            ).start();
                            break;
                        case MessageTopic.SERVER_REGISTER:
                            new ClientAux(
                                    hostname,
                                    msg.getServerPort(),
                                    this.monitor.registerNewServer(msg.getServerPort())).start();
                            break;
                        case MessageTopic.REQUEST_PROCESSED:
                            this.monitor.requestProcessed(msg);
                            break;
                        case MessageTopic.CLIENT_REGISTER_PENDING:
                            this.monitor.sendMsgToLB(this.monitor.registerNewClient(msg));
                            break;
                        case MessageTopic.REQUEST_ACK:
                            this.monitor.updateServerInfo(msg);
                            break;
                        case MessageTopic.REQUEST_IN_PROCESS:
                            this.monitor.requestInProcess(msg);
                            break;
                        case MessageTopic.REJECTION:
                            this.monitor.requestRejected(msg);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
