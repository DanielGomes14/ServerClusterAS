package Monitor;

public interface IMonitor_Heartbeat {

    void serverDown(int serviceId);

    void LBDown(int serviceId);
}
