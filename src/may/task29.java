package may;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

//java may.task29 "https://jsonplaceholder.typicode.com/todos/1"
public class task29 {
    private static final int BUFFER_SIZE = 1024;
    private static final int LINES_PER_SCREEN = 25;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Использование: java task29 <URL>");
            return;
        }

        String url = args[0];
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 80 : uri.getPort();
            String path = uri.getPath().isEmpty() ? "/" : uri.getPath();

            String request = "GET " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Connection: close\r\n\r\n";

            try (AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open()) {
                Future<Void> connectFuture = socketChannel.connect(new InetSocketAddress(host, port));
                connectFuture.get();

                ByteBuffer requestBuffer = ByteBuffer.wrap(request.getBytes(StandardCharsets.UTF_8));
                socketChannel.write(requestBuffer).get();

                ByteBuffer responseBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                int linesPrinted = 0;
                StringBuilder response = new StringBuilder();

                while (true) {
                    Future<Integer> readFuture = socketChannel.read(responseBuffer);
                    int bytesRead = readFuture.get();
                    if (bytesRead == -1) {
                        break;
                    }

                    responseBuffer.flip();
                    String data = StandardCharsets.UTF_8.decode(responseBuffer).toString();
                    response.append(data);

                    String[] lines = response.toString().split("\r\n");
                    for (String line : lines) {
                        if (linesPrinted >= LINES_PER_SCREEN) {
                            System.out.print("Press space to scroll down...");
                            new Scanner(System.in).nextLine();
                            linesPrinted = 0;
                        }
                        System.out.println(line);
                        linesPrinted++;
                    }
                    response.setLength(0); // Очищаем буфер
                    responseBuffer.clear();
                }
            }
        } catch (URISyntaxException | IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}