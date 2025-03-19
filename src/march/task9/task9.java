package march.task9;

public class task9 {
// java march/task9/task 9
    private static final int NUM_STEPS = Integer.MAX_VALUE;
    private static volatile boolean isInterrupted = false;
    private static double pi = 0.0;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Использование: java PiCalculationWithInterrupt <количество потоков>");
            System.exit(1);
        }

        int numThreads = Integer.parseInt(args[0]);
        Thread[] threads = new Thread[numThreads];
        double[] partialSums = new double[numThreads];

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isInterrupted = true;
            System.out.println("\nПолучен сигнал завершения. Завершаем вычисления...");

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (double sum : partialSums) {
                pi += sum;
            }

            pi *= 4.0;
            System.out.printf("Вычисленное значение Pi: %.15f%n", pi);
        }));

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                double sum = 0.0;
                int iteration = 0;

                while (!isInterrupted && iteration < NUM_STEPS) {
                    sum += 1.0 / (iteration * 4.0 + 1.0);
                    sum -= 1.0 / (iteration * 4.0 + 3.0);
                    iteration++;

                    if (iteration % 1_000_000 == 0 && isInterrupted) {
                        break;
                    }
                }

                partialSums[threadIndex] = sum;
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
