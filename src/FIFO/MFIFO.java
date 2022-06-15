package FIFO;

import Communication.Message;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MFIFO {
    private int idxPut = 0;
    private int idxGet = 0;
    private int count = 0;

    private final Message fifo[];
    private final int size;
    private final ReentrantLock rl;
    private final Condition cNotFull;
    private final Condition cNotEmpty;

    private boolean recordsAvailable = true;

    public MFIFO(int size) {
        this.size = size;
        this.fifo = new Message[ size ];
        this.rl = new ReentrantLock();
        this.cNotEmpty = rl.newCondition();
        this.cNotFull = rl.newCondition();
    }

    public void put(Message record) {
        try {
            rl.lock();
            while ( isFull() )
                cNotFull.await();
            fifo[ idxPut ] = record;
            idxPut = (++idxPut) % size;
            count++;
            cNotEmpty.signal();
        } catch ( InterruptedException ignored) {}
        finally {
            rl.unlock();
        }
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
        return count == size;
    }

    public boolean isEmpty() {
        return count == 0;
    }
}