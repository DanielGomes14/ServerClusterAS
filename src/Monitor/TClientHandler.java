package Monitor;

import Communication.Message;
import Communication.MessageTopic;
import Server.ServerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TClientHandler extends Thread {

    private  final Socket clientSocket;
    private  final Monitor monitor;
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
                    System.out.println(msg.getTopic());
                    switch ((msg.getTopic())){
                        case MessageTopic.REQUEST:
                            // receive request from a client
                            this.monitor.receiveNewRequest(msg);
                            // send information of current servers
                            // plus the request itself
                            // to the primary lb
                            msg.setTopic(MessageTopic.SERVERS_INFO);
                            msg.setServersInfo(this.monitor.getServersInfo());
                            sendMsg(msg);
                            break;
                        case MessageTopic.LB_REGISTER:
                            this.monitor.registerLoadBalancer(msg);
                            break;
                        case MessageTopic.SERVER_REGISTER:
                            this.monitor.registerNewServer(new ServerInfo(msg.getServerId(), msg.getServerPort(), 0));
                        case MessageTopic.REQUEST_PROCESSED:
                            this.monitor.requestProcessed(msg);
                            break;
                        case MessageTopic.REJECTION:
                            //
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
