package may;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class task26 {
    private static final int QUEUE_CAPACITY = 10;
    private static final int MAX_MESSAGE_LENGTH = 80;

    private Queue<String> queue = new LinkedList<>();
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();
    private boolean isDropped = false;

    // Инициализация очереди
    public void mymsginit() {
        System.out.println("Очередь инициализирована.");
    }

    // Разблокировка ожидающих операций
    public void mymsgdrop() {
        lock.lock();
        try {
            isDropped = true;
            notFull.signalAll();
            notEmpty.signalAll();
            System.out.println("Очередь разблокирована.");
        } finally {
            lock.unlock();
        }
    }

    // Уничтожение очереди
    public void mymsgdestroy() {
        queue.clear();
        System.out.println("Очередь уничтожена.");
    }

    // Добавление сообщения в очередь
    public int mymsgput(String msg) {
        lock.lock();
        try {
            while (queue.size() >= QUEUE_CAPACITY && !isDropped) {
                notFull.await();
            }

            if (isDropped) {
                return 0;
            }

            String truncatedMsg = msg.length() > MAX_MESSAGE_LENGTH ? msg.substring(0, MAX_MESSAGE_LENGTH) : msg;
            queue.offer(truncatedMsg);
            System.out.println("Добавлено сообщение: " + truncatedMsg);

            notEmpty.signal();
            return truncatedMsg.length();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        } finally {
            lock.unlock();
        }
    }

    // Извлечение сообщения из очереди
    public int mymsgget(byte[] buf, int bufsize) {
        lock.lock();
        try {
            while (queue.isEmpty() && !isDropped) {
                notEmpty.await();
            }

            if (isDropped) {
                return 0;
            }

            String msg = queue.poll();
            if (msg == null) {
                return 0;
            }

            byte[] msgBytes = msg.getBytes();
            int length = Math.min(msgBytes.length, bufsize);
            System.arraycopy(msgBytes, 0, buf, 0, length);

            System.out.println("Извлечено сообщение: " + msg);

            notFull.signal();
            return length;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        task26 queue = new task26();
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
