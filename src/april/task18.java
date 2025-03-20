package april;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class task18 {
    private static LinkedList<Node> list = new LinkedList<>();

    private static class Node {
        String value;
        Lock lock = new ReentrantLock();

        Node(String value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        Thread sortingThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
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
        Node newNode = new Node(input);
        list.addFirst(newNode);
    }

    private static void printList() {
        for (Node node : list) {
            node.lock.lock();
            try {
                System.out.println(node.value);
            } finally {
                node.lock.unlock();
            }
        }
    }

    private static void sortList() {
        boolean swapped;
        do {
            swapped = false;
            for (int i = 0; i < list.size() - 1; i++) {
                Node current = list.get(i);
                Node next = list.get(i + 1);

                current.lock.lock();
                next.lock.lock();
                try {
                    if (current.value.compareTo(next.value) > 0) {
                        String temp = current.value;
                        current.value = next.value;
                        next.value = temp;
                        swapped = true;
                    }
                } finally {
                    next.lock.unlock();
                    current.lock.unlock();
                }
            }
        } while (swapped);
    }
}