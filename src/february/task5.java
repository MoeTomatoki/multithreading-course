package february;

public class task5 {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("Дочерний поток работает");
                    Thread.sleep(500);
                }
                } catch (InterruptedException e) {
                System.out.println("Дочерний поток завершает работу");
            } finally {
                System.out.println("Дочерний поток завершен");
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
