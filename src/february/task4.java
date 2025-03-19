package february;

public class task4 {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            while (true) {
                System.out.println("Дочерний поток работает");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Дочерний поток прерван");
                    break;
                }
            }
        });

        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread.interrupt();
    }
}
