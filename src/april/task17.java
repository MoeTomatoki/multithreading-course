package april;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class task17 {
    private static LinkedList<String> list = new LinkedList<>();
    private static Lock lock = new ReentrantLock();

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

            lock.lock();
            try {
                if (input.isEmpty()) {
                    System.out.println("Текущий список:");
                    for (String item : list) {
                        System.out.println(item);
                    }
                } else {
                    if (input.length() > 80) {
                        for (int i = 0; i < input.length(); i += 80) {
                            list.addFirst(input.substring(i, Math.min(i + 80, input.length())));
                        }
                    } else {
                        list.addFirst(input);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static void sortList() {
        lock.lock();
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
                }
            } while (swapped);
        } finally {
            lock.unlock();
        }
    }
}