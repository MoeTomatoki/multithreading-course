package may;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

//java may.task30 "https://jsonplaceholder.typicode.com/todos/1"
public class task30 {
    private static final int LINES_PER_SCREEN = 25;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Использование: java task30 <URL>");
            return;
        }

        String url = args[0];
        try {
            URI uri = new URI(url);
            URL httpUrl = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("GET");

            Thread readerThread = new Thread(() -> {
                try (InputStream inputStream = connection.getInputStream();
                     Scanner scanner = new Scanner(inputStream)) {
                    int linesPrinted = 0;

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        System.out.println(line);
                        linesPrinted++;

                        if (linesPrinted >= LINES_PER_SCREEN) {
                            synchronized (System.in) {
                                System.out.print("Press space to scroll down...");
                                System.in.wait();
                            }
                            linesPrinted = 0;
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            Thread userThread = new Thread(() -> {
                Scanner userInput = new Scanner(System.in);
                while (true) {
                    String input = userInput.nextLine();
                    if (input.equals(" ")) {
                        synchronized (System.in) {
                            System.in.notify();
                        }
                    }
                }
            });

            readerThread.start();
            userThread.start();

            readerThread.join();
            userThread.interrupt();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}