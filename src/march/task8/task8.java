package march;

public class task8 {

    private static final int NUM_STEPS = 200000000; // Количество итераций
    private static double pi = 0.0;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Использование: java PiCalculation <количество потоков>");
            System.exit(1);
        }

        int numThreads = Integer.parseInt(args[0]);
        Thread[] threads = new Thread[numThreads];
        double[] partialSums = new double[numThreads];

        int stepsPerThread = NUM_STEPS / numThreads;
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                double sum = 0.0;
                int start = threadIndex * stepsPerThread;
                int end = (threadIndex == numThreads - 1) ? NUM_STEPS : start + stepsPerThread;

                for (int j = start; j < end; j++) {
                    sum += 1.0 / (j * 4.0 + 1.0);
                    sum -= 1.0 / (j * 4.0 + 3.0);
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

        for (double sum : partialSums) {
            pi += sum;
        }

        pi *= 4.0;
        System.out.printf("Вычисленное значение Pi: %.15f%n", pi);
    }
}
