package april;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class task23 {
    private static LinkedList<String> sortedList = new LinkedList<>();
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        String[] inputStrings = {"apple", "banana", "cherry", "date", "elderberry"};

        Thread[] threads = new Thread[inputStrings.length];
        for (int i = 0; i < inputStrings.length; i++) {
            String str = inputStrings[i];
            threads[i] = new Thread(() -> addToList(str));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Отсортированный список:");
        for (String str : sortedList) {
            System.out.println(str);
        }
    }

    private static void addToList(String str) {
        try {
            Thread.sleep(str.length() * 100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lock.lock();
        try {
            sortedList.add(str);
        } finally {
            lock.unlock();
        }
    }
}