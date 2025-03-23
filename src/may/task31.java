package may;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class task31 {
    private static final int BUFFER_SIZE = 1024;
    private static final Map<String, byte[]> cache = new HashMap<>();

    public static void main(String[] args) {
        int proxyPort = 8080;

        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(proxyPort));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Прокси запущен на порту " + proxyPort);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isAcceptable()) {
                        acceptConnection(serverSocketChannel, selector);
                    } else if (key.isReadable()) {
                        handleClientRequest(key);
                    } else if (key.isWritable()) {
                        sendResponseToClient(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptConnection(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Принято новое соединение: " + clientChannel.getRemoteAddress());
    }

    private static void handleClientRequest(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clientChannel.close();
            System.out.println("Соединение закрыто: " + clientChannel.getRemoteAddress());
            return;
        }

        buffer.flip();
        String request = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println("Получен запрос: " + request);

        String url = extractUrlFromRequest(request);
        if (url == null) {
            clientChannel.close();
            return;
        }

        String host = extractHostFromRequest(request);
        if (host == null) {
            clientChannel.close();
            return;
        }

        byte[] cachedResponse = cache.get(url);
        if (cachedResponse != null) {
            ByteBuffer responseBuffer = ByteBuffer.wrap(cachedResponse);
            clientChannel.write(responseBuffer);
            clientChannel.close();
            System.out.println("Ответ из кэша отправлен: " + url);
            return;
        }

        SocketChannel remoteChannel = SocketChannel.open();
        remoteChannel.configureBlocking(false);
        remoteChannel.connect(new InetSocketAddress(host, 80));

        clientChannel.register(key.selector(), SelectionKey.OP_WRITE, remoteChannel);
        remoteChannel.register(key.selector(), SelectionKey.OP_CONNECT, clientChannel);
    }

    private static String extractHostFromRequest(String request) {
        String[] lines = request.split("\r\n");
        for (String line : lines) {
            if (line.toLowerCase().startsWith("host:")) {
                return line.split(":")[1].trim();
            }
        }
        return null;
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

        if (parts[1].equals("/")) {
            return "http://" + extractHostFromRequest(request) + "/";
        }

        return parts[1];
    }

    private static void sendResponseToClient(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        SocketChannel remoteChannel = (SocketChannel) key.attachment();

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = remoteChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            remoteChannel.close();
            System.out.println("Соединение закрыто: " + clientChannel.getRemoteAddress());
            return;
        }

        buffer.flip();
        clientChannel.write(buffer);
    }
}