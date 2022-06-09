package Communication;

import java.io.ObjectInputStream;
import java.net.Socket;

public class TClientHandler implements  Runnable{

    private  final Socket clientSocket;
    private ObjectInputStream in = null;

    public TClientHandler(Socket socket) {
        this.clientSocket = socket;
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
