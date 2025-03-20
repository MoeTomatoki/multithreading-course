package april;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class task21 {
    private static LinkedList<String> list = new LinkedList<>();
    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        Thread sortingThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    sortList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        sortingThread.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Введите строку: ");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                printList();
            } else {
                addToList(input);
            }
        }
    }

    private static void addToList(String input) {
        lock.writeLock().lock();
        try {
            if (input.length() > 80) {
                for (int i = 0; i < input.length(); i += 80) {
                    list.addFirst(input.substring(i, Math.min(i + 80, input.length())));
                }
            } else {
                list.addFirst(input);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static void printList() {
        lock.readLock().lock();
        try {
            System.out.println("Текущий список:");
            for (String item : list) {
                System.out.println(item);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private static void sortList() {
        lock.writeLock().lock();
        try {
            boolean swapped;
            do {
                swapped = false;
                for (int i = 0; i < list.size() - 1; i++) {
                    if (list.get(i).compareTo(list.get(i + 1)) > 0) {
                        String temp = list.get(i);
                        list.set(i, list.get(i + 1));
                        list.set(i + 1, temp);
                        swapped = true;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (swapped);
        } finally {
            lock.writeLock().unlock();
        }
    }
}