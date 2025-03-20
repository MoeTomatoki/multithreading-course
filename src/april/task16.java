package april;

import java.util.concurrent.Semaphore;

public class task16 {
    private static final int NUM_LINES = 10;

    public static void main(String[] args) throws InterruptedException {
        Semaphore parentSemaphore = new Semaphore(1);
        Semaphore childSemaphore = new Semaphore(0);

        ProcessBuilder pb = new ProcessBuilder("java", "ChildProcess");
        pb.inheritIO();

        try {
            Process childProcess = pb.start();

            for (int i = 0; i < NUM_LINES; i++) {
                parentSemaphore.acquire();
                System.out.println("Родительский процесс: " + i);
                childSemaphore.release();
            }

            childProcess.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ChildProcess {
    private static final int NUM_LINES = 10;
    public static void main(String[] args) throws InterruptedException {
        Semaphore parentSemaphore = new Semaphore(0);
        Semaphore childSemaphore = new Semaphore(1);

        for (int i = 0; i < NUM_LINES; i++) {
            childSemaphore.acquire();
            System.out.println("Дочерний процесс: " + i);
            parentSemaphore.release();
        }
    }
}