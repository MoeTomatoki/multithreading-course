package may;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

//java may.task28 "https://jsonplaceholder.typicode.com/todos/1"
public class task28 {
    private static final int BUFFER_SIZE = 1024;
    private static final int LINES_PER_SCREEN = 25;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Использование: java task28 <URL>");
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

            try (Selector selector = Selector.open();
                 SocketChannel socketChannel = SocketChannel.open()) {

                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress(host, port));
                socketChannel.register(selector, SelectionKey.OP_CONNECT);

                boolean isConnected = false;
                boolean isReadingBody = false;
                int linesPrinted = 0;

                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                StringBuilder response = new StringBuilder();

                while (true) {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKeys.iterator();

                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        if (key.isConnectable()) {
                            if (socketChannel.finishConnect()) {
                                key.interestOps(SelectionKey.OP_WRITE);
                                isConnected = true;
                            }
                        } else if (key.isWritable() && isConnected) {
                            socketChannel.write(ByteBuffer.wrap(request.getBytes(StandardCharsets.UTF_8)));
                            key.interestOps(SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            buffer.clear();
                            int bytesRead = socketChannel.read(buffer);
                            if (bytesRead == -1) {
                                return;
                            }

                            buffer.flip();
                            String data = StandardCharsets.UTF_8.decode(buffer).toString();
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
                            response.setLength(0);
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}