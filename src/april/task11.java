package april;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class task11 {
    private static final int NUM_LINES = 10;
    private static Lock parentLock = new ReentrantLock();
    private static Lock childLock = new ReentrantLock();

    public static void main(String[] args) {
        childLock.lock();

        Thread childThread = new Thread(() -> {
            for (int i = 0; i < NUM_LINES; i++) {
                childLock.lock();
                System.out.println("Дочерний поток: " + i);
                parentLock.unlock();
            }
        });

        childThread.start();

        for (int i = 0; i < NUM_LINES; i++) {
            parentLock.lock();
            System.out.println("Родительский поток: " + i);
            childLock.unlock();
        }
    }
}