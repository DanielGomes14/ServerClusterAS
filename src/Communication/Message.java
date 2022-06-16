package Communication;

import java.io.Serializable;

public class Message implements Serializable {
    private int topic;

    private int requestId;

    private int clientId;

    private int serverId;

    /**  Number of iterations */
    private int na;

    private double pi;

    public Message(int topic) {
        this.topic = topic;
    }

    public Message(int topic, int requestId, int clientId, int serverId, int na) {
        this.topic = topic;
        this.requestId = requestId;
        this.clientId = clientId;
        this.serverId = serverId;
        this.na = na;
    }

    public Message() {
    }


    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
        this.topic = topic;
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

    public int getNa() {
        return na;
    }

    public void setNa(int na) {
        this.na = na;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }
}
