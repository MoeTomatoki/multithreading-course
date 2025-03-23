package may;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class task32 {
    private static final Map<String, byte[]> cache = new HashMap<>();

    public static void main(String[] args) {
        int proxyPort = 8080;

        try (ServerSocket serverSocket = new ServerSocket(proxyPort)) {
            System.out.println("Прокси запущен на порту " + proxyPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Принято новое соединение: " + clientSocket.getRemoteSocketAddress());

                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (InputStream clientInput = clientSocket.getInputStream();
             OutputStream clientOutput = clientSocket.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead = clientInput.read(buffer);
            if (bytesRead == -1) {
                return;
            }

            String request = new String(buffer, 0, bytesRead);
            System.out.println("Получен запрос: " + request);

            String url = extractUrlFromRequest(request);
            if (url == null) {
                return;
            }

            byte[] cachedResponse = cache.get(url);
            if (cachedResponse != null) {
                clientOutput.write(cachedResponse);
                System.out.println("Ответ из кэша отправлен: " + url);
                return;
            }

            try (Socket remoteSocket = new Socket(extractHostFromUrl(url), 80);
                 InputStream remoteInput = remoteSocket.getInputStream();
                 OutputStream remoteOutput = remoteSocket.getOutputStream()) {

                remoteOutput.write(buffer, 0, bytesRead);

                byte[] responseBuffer = new byte[1024];
                int responseBytesRead;
                while ((responseBytesRead = remoteInput.read(responseBuffer)) != -1) {
                    clientOutput.write(responseBuffer, 0, responseBytesRead);
                }

                cache.put(url, responseBuffer);
                System.out.println("Ответ кэширован: " + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractUrlFromRequest(String request) {
        String[] lines = request.split("\r\n");
        if (lines.length == 0) {
            return null;
        }

        String[] parts = lines[0].split(" ");
        if (parts.length < 2) {
            return null;
        }

        return parts[1];
    }

    private static String extractHostFromUrl(String url) {
        return url.split("/")[2];
    }
}