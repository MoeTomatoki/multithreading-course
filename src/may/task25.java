package may;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class task25 {
    private static final int QUEUE_CAPACITY = 10;
    private static final int MAX_MESSAGE_LENGTH = 80;

    private Queue<String> queue = new LinkedList<>();
    private Semaphore emptySlots = new Semaphore(QUEUE_CAPACITY);
    private Semaphore filledSlots = new Semaphore(0);
    private Semaphore mutex = new Semaphore(1);
    private boolean isDropped = false;

    // Инициализация очереди
    public void mymsginit() {
        System.out.println("Очередь инициализирована.");
    }

    // Разблокировка ожидающих операций
    public void mymsgdrop() {
        isDropped = true;
        emptySlots.release(QUEUE_CAPACITY);
        filledSlots.release(QUEUE_CAPACITY);
        System.out.println("Очередь разблокирована.");
    }

    // Уничтожение очереди
    public void mymsgdestroy() {
        queue.clear();
        System.out.println("Очередь уничтожена.");
    }

    // Добавление сообщения в очередь
    public int mymsgput(String msg) {
        if (isDropped) {
            return 0;
        }

        try {
            emptySlots.acquire();
            mutex.acquire();

            if (isDropped) {
                mutex.release();
                emptySlots.release();
                return 0;
            }

            String truncatedMsg = msg.length() > MAX_MESSAGE_LENGTH ? msg.substring(0, MAX_MESSAGE_LENGTH) : msg;
            queue.offer(truncatedMsg);
            System.out.println("Добавлено сообщение: " + truncatedMsg);

            mutex.release();
            filledSlots.release();

            return truncatedMsg.length();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Извлечение сообщения из очереди
    public int mymsgget(byte[] buf, int bufsize) {
        if (isDropped) {
            return 0;
        }

        try {
            filledSlots.acquire();
            mutex.acquire();

            if (isDropped) {
                mutex.release();
                filledSlots.release();
                return 0;
            }

            String msg = queue.poll();
            if (msg == null) {
                mutex.release();
                filledSlots.release();
                return 0;
            }

            // Обрезаем сообщение до размера буфера
            byte[] msgBytes = msg.getBytes();
            int length = Math.min(msgBytes.length, bufsize);
            System.arraycopy(msgBytes, 0, buf, 0, length);

            System.out.println("Извлечено сообщение: " + msg);

            mutex.release();
            emptySlots.release();

            return length;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void main(String[] args) {
        task25 queue = new task25();
        queue.mymsginit();

        Thread producer1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                queue.mymsgput("Сообщение от производителя 1: " + i);
            }
        });

        Thread producer2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                queue.mymsgput("Сообщение от производителя 2: " + i);
            }
        });

        Thread consumer1 = new Thread(() -> {
            byte[] buf = new byte[MAX_MESSAGE_LENGTH];
            for (int i = 0; i < 5; i++) {
                queue.mymsgget(buf, buf.length);
            }
        });

        Thread consumer2 = new Thread(() -> {
            byte[] buf = new byte[MAX_MESSAGE_LENGTH];
            for (int i = 0; i < 5; i++) {
                queue.mymsgget(buf, buf.length);
            }
        });

        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();

        try {
            producer1.join();
            producer2.join();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        queue.mymsgdestroy();
    }
}
