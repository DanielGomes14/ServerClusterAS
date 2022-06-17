package Communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Server.ServerInfo;

public class Message implements Serializable {
    private int topic;

    private int requestId;

    private int clientId;

    private int serverId;

    private int serverPort;

    /**  Number of iterations */
    private int NI;

    private int deadline;

    private double pi;

    private Map<Integer, ServerInfo> serversInfo;

    /** When a Server/LB crashes this variable will be used to send a batch of all pending Requests**/
    private List<Message> pendingRequests;
    public Message(int topic) {
        this.topic = topic;
    }

    public Message(int topic, int requestId, int clientId, int serverId, int NI, int deadline) {
        this.topic = topic;
        this.requestId = requestId;
        this.clientId = clientId;
        this.serverId = serverId;
        this.NI = NI;
        this.deadline = deadline;
    }

    public Message() {
    }

    public Message(int topic, int port) {
        this.topic = topic;
        this.serverPort = port;
    }


    public Map<Integer, ServerInfo> getServersInfo() {
        return serversInfo;
    }

    public void setServersInfo(Map<Integer, ServerInfo> serversInfo) {
        this.serversInfo = serversInfo;
    }

    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public  void setPendingRequests(ArrayList<Message> pending){
        this.pendingRequests = pending;
    }
    public  List<Message> getPendingRequests(){
        return this.pendingRequests;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getNI() {
        return NI;
    }

    public void setNI(int NI) {
        this.NI = NI;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }
}
