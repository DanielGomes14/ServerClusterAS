package Monitor;

import Server.ServerInfo;

public interface IMonitor {


    void registerNewServer(int serverId, ServerInfo serverInfo);

    void registerLoadBalancer();

}
