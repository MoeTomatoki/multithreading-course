package may;

import java.util.concurrent.Semaphore;

public class task24 {
    private static final Semaphore semaphoreA = new Semaphore(0);
    private static final Semaphore semaphoreB = new Semaphore(0);
    private static final Semaphore semaphoreC = new Semaphore(0);
    private static final Semaphore widgetSemaphore = new Semaphore(0);

    public static void main(String[] args) {
        Thread workerA = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println("Деталь A изготовлена.");
                    semaphoreA.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread workerB = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                    System.out.println("Деталь B изготовлена.");
                    semaphoreB.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread workerC = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                    System.out.println("Деталь C изготовлена.");
                    semaphoreC.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread moduleHandler = new Thread(() -> {
            while (true) {
                try {
                    semaphoreA.acquire();
                    semaphoreB.acquire();
                    System.out.println("Модуль (A + B) собран.");
                    widgetSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread widgetHandler = new Thread(() -> {
            while (true) {
                try {
                    semaphoreC.acquire();
                    widgetSemaphore.acquire();
                    System.out.println("Винтик (C + модуль) собран.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        workerA.start();
        workerB.start();
        workerC.start();
        moduleHandler.start();
        widgetHandler.start();
    }
}
