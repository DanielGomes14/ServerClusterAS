package FIFO;

package FIFO;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


import Communication.Record;

public class MFIFO implements IFIFO_Client, IFIFO_Source {
    private int idxPut = 0;
    private int idxGet = 0;
    private int count = 0;

    private final Record fifo[];
    private final int size;
    private final ReentrantLock rl;
    private final Condition cNotFull;
    private final Condition cNotEmpty;

    private boolean recordsAvailable = true;

    public MFIFO(int size) {
        this.size = size;
        this.fifo = new Record[ size ];
        this.rl = new ReentrantLock();
        this.cNotEmpty = rl.newCondition();
        this.cNotFull = rl.newCondition();
    }

    public void put(Record record) {
        try {
            rl.lock();
            while ( isFull() )
                cNotFull.await();
            fifo[ idxPut ] = record;
            idxPut = (++idxPut) % size;
            count++;
            cNotEmpty.signal();
        } catch ( InterruptedException ex ) {}
        finally {
            rl.unlock();
        }
    }

    public Record get() {
        try{
            rl.lock();
            try {
                while ( isEmpty() ) {
                    if (!recordsAvailable)
                        return new Record(-1, -1.0, -1);
                    cNotEmpty.await();
                }
            } catch( InterruptedException ex ) {}
            idxGet = idxGet % size;
            count --;
            cNotFull.signal();
            return fifo[idxGet++];
        }
        finally {
            rl.unlock();
        }
    }


    public void finishedReadingRecords() {
        try {
            rl.lock();
            recordsAvailable = false;
            cNotEmpty.signalAll();
        }
        finally {
            rl.unlock();
        }
    }

    public boolean isFull() {
        return count == size;
    }

    public boolean isEmpty() {
        return count == 0;
    }
}