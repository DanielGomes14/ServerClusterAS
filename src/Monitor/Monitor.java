package Monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor extends  Thread implements IMonitor, IMonitor_Heartbeat{

    /** Server Information */
    private final Map<Integer, String> servers;

    /** Requests waiting to be assigned to a server. */
    private final Map<Integer, String> pendingRequests;


    private final ReentrantLock rl;
    public Monitor() {
        this.servers = new HashMap<Integer, String>() ;
        this.rl = new ReentrantLock();
        this.pendingRequests = new HashMap<Integer, String>() ;
    }

    public void run() {

    }

    @Override
    public void registerNewServer() {

    }

    @Override
    public void registerLoadBalancer() {

    }

    @Override
    public void registerServerFailure(int serviceId) {
        rl.lock();
        servers.remove(serviceId);
        //TODO: Connect to Primary LB and send pending Requests of the Servers
        pendingRequests.remove(serviceId);
        rl.unlock();
    }
}
