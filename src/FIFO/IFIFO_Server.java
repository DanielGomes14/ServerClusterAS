package FIFO;

import Communication.Message;

public interface IFIFO_Server {

    void put(Message request);
    Message get();
    boolean isFull();
    boolean isEmpty();

    boolean increaseNICounter(int ni);
    void decreaseNICounter(int ni);
}
