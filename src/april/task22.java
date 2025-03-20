package april;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class task22 {
    private static final int PHILOSOPHERS_COUNT = 5;
    private static Lock[] forks = new ReentrantLock[PHILOSOPHERS_COUNT];
    private static Lock table = new ReentrantLock();
    private static Condition condition = table.newCondition();

    public static void main(String[] args) {
        for (int i = 0; i < PHILOSOPHERS_COUNT; i++) {
            forks[i] = new ReentrantLock();
        }

        Thread[] philosophers = new Thread[PHILOSOPHERS_COUNT];
        for (int i = 0; i < PHILOSOPHERS_COUNT; i++) {
            philosophers[i] = new Thread(new Philosopher(i));
            philosophers[i].start();
        }
    }

    static class Philosopher implements Runnable {
        private final int id;
        private final int leftFork;
        private final int rightFork;

        Philosopher(int id) {
            this.id = id;
            this.leftFork = id;
            this.rightFork = (id + 1) % PHILOSOPHERS_COUNT;
        }

        @Override
        public void run() {
            while (true) {
                think();
                eat();
            }
        }

        private void think() {
            System.out.println("Философ " + id + " размышляет");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void eat() {
            table.lock();
            try {
                while (true) {
                    if (forks[leftFork].tryLock()) {
                        if (forks[rightFork].tryLock()) {
                            break;
                        } else {
                            forks[leftFork].unlock();
                        }
                    }
                    condition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                table.unlock();
            }

            System.out.println("Философ " + id + " ест");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            table.lock();
            try {
                forks[leftFork].unlock();
                forks[rightFork].unlock();
                condition.signalAll();
            } finally {
                table.unlock();
            }
        }
    }
}