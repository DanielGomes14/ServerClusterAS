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

            for (int i=0; i<size; i++) {
                if (fifo[i] == null) {
                    fifo[i] = request;
                    break;
                }
            }

            count++;

            this.niCounter += request.getNI();

            cNotEmpty.signal();
        } catch ( InterruptedException ignored) {}
        finally {
            rl.unlock();
        }
    }

    public boolean checkNICounter(int ni){
        boolean success = true;
        rl.lock();
        if (this.niCounter + ni > 20) {
            success = false;
        }
        rl.unlock();
        return  success;
    }


    public Message get() {
        Message req = null;

        try{
            rl.lock();

            while ( isEmpty() )
                cNotEmpty.await();
            int minDeadline = Integer.MAX_VALUE;
            int idxChoosen = -1;
            for (int i=0; i<size; i++) {
                if (fifo[i] != null && minDeadline >= fifo[i].getDeadline()) {
                    minDeadline = fifo[i].getDeadline();
                    req = fifo[i];
                    idxChoosen = i;
                }
            }

            if (req != null) {
                fifo[idxChoosen] = null;
            }

            count --;

            this.niCounter -= req.getNI();

            cNotFull.signal();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rl.unlock();
        }
        
        return req;
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