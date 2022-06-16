package Monitor;

import Communication.ClientAux;
import Communication.Message;

public class HeartbeatManager extends  Thread {
    private final ClientAux clientAux;
    private final int HEATBEAT_TIMEOUT = 1000;
    private final int serviceId; //Id of the Service to be monitored through Heartbeat
    private  final IMonitor_Heartbeat monitor;
    private final boolean isServer;

    public HeartbeatManager(String hostname, int port, int serviceId, boolean isServer, IMonitor_Heartbeat monitor){
        this.clientAux = new ClientAux(hostname,port);
        this.serviceId = serviceId;
        this.monitor = monitor;
        this.isServer = isServer;
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
        // Inform Monitor of Server Failure

        if (isServer)
            this.monitor.serverDown(serviceId);
        else
            this.monitor.LBDown(serviceId);
    }
}
