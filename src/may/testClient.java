package may;

import java.io.*;
import java.net.Socket;

public class testClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 8080;

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String httpRequest = "GET / HTTP/1.1\r\nHost: www.example.com\r\n\r\n";
            out.write(httpRequest.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}