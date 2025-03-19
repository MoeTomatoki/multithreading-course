package february;

public class task3 {
    public static void main(String[] args) {
        String[] messages = {"Поток 1", "Поток 2", "Поток 3", "Поток 4"};

        for (int i = 0; i < 4; i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    System.out.println(messages[index] + ": " + j);
                }
            });
            thread.start();
        }
    }
}
