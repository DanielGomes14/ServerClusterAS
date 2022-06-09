package Monitor;

import Communication.ClientAux;
import Communication.Message;

public class HeartbeatManager extends  Thread {
    private ClientAux clientAux;
    private final int HEATBEAT_TIMEOUT = 1000;
    private final int serviceId; //Id of the Service to be monitored through Heartbeat
    public HeartbeatManager(String hostname, int port, int serviceId){
        this.clientAux = new ClientAux(hostname,port);
        this.serviceId = serviceId;
    }


    public boolean sendHeartBeat(Message msg){
        try{
            clientAux.sendMsg(msg);
        }
        catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
        return  true;
    }

    @Override
    public void run() {
        clientAux.start();
        Message heartbeatMessage = new Message();
        while(sendHeartBeat(heartbeatMessage)){
            try {
                Thread.sleep(HEATBEAT_TIMEOUT);
            } catch (InterruptedException ex) {
                System.out.println(ex.toString());
            }
        }

    }
}
