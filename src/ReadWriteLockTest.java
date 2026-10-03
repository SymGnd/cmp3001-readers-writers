import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ReadWriteLockTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ReadWriteLock RW = new ReadWriteLock();

        for (int i = 0; i < 4; i++) {
            executorService.execute(new Writer(RW));
        }

        for (int i = 0; i < 4; i++) {
            executorService.execute(new Reader(RW));
        }

        // Shutting down the executor service after some time for simplicity
        executorService.shutdown();
    }
}

class ReadWriteLock {
    private Semaphore S = new Semaphore(1);
    private Semaphore mutex = new Semaphore(1);
    private int readers = 0;

    public void readLock() {
        try {
            mutex.acquire();
            readers++;
            if (readers == 1) {
                S.acquire();
            }
            mutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void readUnLock() {
        try {
            mutex.acquire();
            readers--;
            if (readers == 0) {
                S.release();
            }
            mutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeLock() {
        try {
            S.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeUnLock() {
        S.release();
    }
}

class Writer implements Runnable {
    private ReadWriteLock RW_lock;

    public Writer(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (true) {
            RW_lock.writeLock();
            System.out.println(Thread.currentThread().getName() + " is writing");
            RW_lock.writeUnLock();
            try {
                Thread.sleep(1000); // Simulating some writing time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Reader implements Runnable {
    private ReadWriteLock RW_lock;

    public Reader(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (true) {
            RW_lock.readLock();
            System.out.println(Thread.currentThread().getName() + " is reading");
            RW_lock.readUnLock();
            try {
                Thread.sleep(500); // Simulating some reading time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
