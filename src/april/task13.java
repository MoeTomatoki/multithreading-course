package april;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class task13 {
    private static final int NUM_LINES = 10;
    private static Lock lock = new ReentrantLock();
    private static Condition parentCondition = lock.newCondition();
    private static Condition childCondition = lock.newCondition();
    private static boolean isParentTurn = true;

    public static void main(String[] args) {
        Thread childThread = new Thread(() -> {
            for (int i = 0; i < NUM_LINES; i++) {
                lock.lock();
                try {
                    while (isParentTurn) {
                        childCondition.await();
                    }
                    System.out.println("Дочерний поток: " + i);
                    isParentTurn = true;
                    parentCondition.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        });

        childThread.start();

        for (int i = 0; i < NUM_LINES; i++) {
            lock.lock();
            try {
                while (!isParentTurn) {
                    parentCondition.await();
                }
                System.out.println("Родительский поток: " + i);
                isParentTurn = false;
                childCondition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}