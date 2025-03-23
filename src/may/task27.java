package may;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

//java may.task27 8080 example.com 80
public class task27 {
    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_CONNECTIONS = 510;

    private int listenPort;
    private String remoteHost;
    private int remotePort;

    public task27(int listenPort, String remoteHost, int remotePort) {
        this.listenPort = listenPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public void start() {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(listenPort));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Сервер запущен на порту " + listenPort);

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
                        readData(key);
                    } else if (key.isWritable()) {
                        writeData(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptConnection(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);

        SocketChannel remoteChannel = SocketChannel.open();
        remoteChannel.configureBlocking(false);
        remoteChannel.connect(new InetSocketAddress(remoteHost, remotePort));

        clientChannel.register(selector, SelectionKey.OP_READ, remoteChannel);
        remoteChannel.register(selector, SelectionKey.OP_READ, clientChannel);

        System.out.println("Принято новое соединение: " + clientChannel.getRemoteAddress());
    }

    private void readData(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        SocketChannel peerChannel = (SocketChannel) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) {
            channel.close();
            peerChannel.close();
            System.out.println("Соединение закрыто: " + channel.getRemoteAddress());
            return;
        }

        if (bytesRead > 0) {
            buffer.flip();
            peerChannel.write(buffer);
        }
    }

    private void writeData(SelectionKey key) throws IOException {
        // В данной реализации запись данных не требуется из-за того, что мы используем read/write в одном потоке
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Использование: java task27 <порт> <удаленный хост> <удаленный порт>");
            return;
        }

        int listenPort = Integer.parseInt(args[0]);
        String remoteHost = args[1];
        int remotePort = Integer.parseInt(args[2]);

        task27 server = new task27(listenPort, remoteHost, remotePort);
        server.start();
    }
}