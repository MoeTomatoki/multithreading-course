package march;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class tasks10 {

    private static final int PHILO = 5;
    private static final int FOOD = 50;
    private static final int DELAY = 30;

    private static Lock[] forks = new ReentrantLock[PHILO];
    private static int food = FOOD;

    public static void main(String[] args) {
        for (int i = 0; i < PHILO; i++) {
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < PHILO; i++) {
            final int philosopherId = i;
            new Thread(() -> philosopher(philosopherId)).start();
        }
    }

    private static void philosopher(int id) {
        int leftFork = id;
        int rightFork = (id + 1) % PHILO;

        System.out.println("Философ " + id++ + "приступил к приему пищи");

        while (true) {
            int dish = getFood();
            if (dish == 0) {
                break;
            }

            System.out.println("Философ " + id++ + ": приступил к приему пищи номер " + dish++);

            if (id % 2 == 0) {
                takeFork(id, leftFork, "левая");
                takeFork(id, rightFork, "правая");
            } else {
                takeFork(id, rightFork, "правая");
                takeFork(id, leftFork, "левая");
            }

            System.out.println("Философ " + id++ + ": ест.");
            try {
                Thread.sleep(DELAY * (FOOD - dish + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Освобождение вилок
            releaseFork(leftFork);
            releaseFork(rightFork);

            System.out.println("Философ " + id++ + " закончил есть");
        }

        System.out.println("Философ  " + id++ + " покинул общий стол");
    }

    private static synchronized int getFood() {
        if (food > 0) {
            food--;
            return food;
        }
        return 0;
    }

    private static void takeFork(int philosopherId, int forkId, String hand) {
        forks[forkId].lock();
        System.out.println("Philosopher " + philosopherId + ": got " + hand + " fork " + forkId);
    }

    private static void releaseFork(int forkId) {
        forks[forkId].unlock();
    }
}
