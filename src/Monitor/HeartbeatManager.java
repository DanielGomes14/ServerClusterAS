package Monitor;

import Communication.ClientAux;
import Communication.Message;

public class HeartbeatManager extends  Thread {
    private final ClientAux clientAux;
    private final int HEATBEAT_TIMEOUT = 1000;
    private final int serviceId; //Id of the Service to be monitored through Heartbeat
    private  final  Monitor monitor;
    public HeartbeatManager(String hostname, int port, int serviceId, Monitor monitor){
        this.clientAux = new ClientAux(hostname,port);
        this.serviceId = serviceId;
        this.monitor = monitor;
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
        this.monitor.serverDown(serviceId);
        // Inform Monitor of Server Failure
    }
}
