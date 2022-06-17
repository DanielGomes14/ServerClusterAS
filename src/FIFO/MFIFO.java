package FIFO;

import Communication.Message;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MFIFO implements  IFIFO_Server{
    private int idxPut = 0;
    private int idxGet = 0;
    private int count = 0;

    private final Message fifo[];
    private final int size;
    private final ReentrantLock rl;
    private final Condition cNotFull;
    private final Condition cNotEmpty;
    private int niCounter;

    public MFIFO(int size){
        this.size = size;
        this.fifo = new Message[ size ];
        this.rl = new ReentrantLock();
        this.cNotEmpty = rl.newCondition();
        this.cNotFull = rl.newCondition();
        this.niCounter = 0;
    }

    public void put(Message request) {
        try {
            rl.lock();
            while ( isFull() )
                cNotFull.await();
            fifo[ idxPut ] = request;
            idxPut = (++idxPut) % size;
            count++;
            cNotEmpty.signal();
        } catch ( InterruptedException ignored) {}
        finally {
            rl.unlock();
        }
    }

    public boolean increaseNICounter(int ni){
        boolean success = true;
        rl.lock();
        if(this.niCounter + ni > 20) {
            success = false;
        }
        else
            this.niCounter+=ni;
        rl.unlock();
        return  success;
    }
    public void decreaseNICounter(int ni){
        rl.lock();
        this.niCounter = ni;
        rl.unlock();
    }
    public Message get() {
        try{
            rl.lock();
            while ( isEmpty() )
                cNotEmpty.await();
            idxGet = idxGet % size;
            count --;
            cNotFull.signal();
            return fifo[idxGet++];
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rl.unlock();
        }
        return null;
    }

    public boolean isFull() {
        this.rl.lock();
        boolean res = count == size;
        this.rl.unlock();
        return res;
    }
    public boolean isEmpty() {
        this.rl.lock();
        boolean res = count == 0;
        this.rl.unlock();
        return res;
    }
}