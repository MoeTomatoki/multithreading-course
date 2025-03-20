package april;

import java.util.concurrent.Semaphore;

public class task14 {
    private static final int NUM_LINES = 10;
    private static Semaphore parentSemaphore = new Semaphore(1);
    private static Semaphore childSemaphore = new Semaphore(0);

    public static void main(String[] args) {
        Thread childThread = new Thread(() -> {
            for (int i = 0; i < NUM_LINES; i++) {
                try {
                    childSemaphore.acquire();
                    System.out.println("Дочерний поток: " + i);
                    parentSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        childThread.start();

        for (int i = 0; i < NUM_LINES; i++) {
            try {
                parentSemaphore.acquire();
                System.out.println("Родительский поток: " + i);
                childSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}