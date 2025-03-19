package march;

import java.util.Scanner;

public class task6 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строки (не более 100). Для завершения ввода введите 'end':");

        // Чтение строк с консоли
        String[] lines = new String[100];
        int count = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("end")) {
                break;
            }
            if (count < 100) {
                lines[count] = line;
                count++;
            }
        }
        scanner.close();

        // Создание и запуск потоков для каждой строки
        for (int i = 0; i < count; i++) {
            final String currentLine = lines[i];
            Thread thread = new Thread(() -> {
                try {
                    // Задержка пропорциональна длине строки
                    Thread.sleep(currentLine.length() * 10L); // Коэффициент 10 мс на символ
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Вывод строки
                System.out.println(currentLine);
            });
            thread.start();
        }
    }
}
