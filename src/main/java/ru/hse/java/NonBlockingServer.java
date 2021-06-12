package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NonBlockingServer {
    private final ExecutorService receivingThread = Executors.newSingleThreadExecutor();
    private final ExecutorService sendingThread = Executors.newSingleThreadExecutor();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2);

    private volatile boolean isWorking = false;

    private ServerSocketChannel serverSocketChannel;

    private final ConcurrentHashMap<SocketChannel, ClientData> clients = new ConcurrentHashMap<>();

    private Selector receivingSelector;
    private Selector sendingSelector;

    private final int x = 2;

    public void run() throws IOException {
        isWorking = true;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT));
        receivingThread.submit(this::receiveMessages);
        sendingThread.submit(this::sendMessages);
    }

    public void close() throws IOException {
        isWorking = false;
        serverSocketChannel.close();
    }

    public void receiveMessages() {
        try {
            receivingSelector = Selector.open();
            serverSocketChannel.register(receivingSelector, serverSocketChannel.validOps());
            while (isWorking) {
                if (receivingSelector.select() <= 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = receivingSelector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        clients.put(socketChannel, new ClientData(socketChannel));
                        System.out.println("client accepted");
                    }
                    if (key.isReadable()) {
                        ClientData client = clients.get((SocketChannel) key.channel());
                        client.receiveFromClient();
                        System.out.println("receive data from client");
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessages() {
        try {
            sendingSelector = Selector.open();
            serverSocketChannel.register(sendingSelector, serverSocketChannel.validOps());
            while (isWorking) {
                if (sendingSelector.select() <= 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = sendingSelector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isWritable()) {
                        ClientData client = clients.get((SocketChannel) key.channel());
                        if (client.isReady) {
                            client.sendToClient();
                            client.close();
                            System.out.println("send data to client");
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientData {

        private int[] data;

        private volatile boolean isReady;

        private final SocketChannel socketChannel;

        private int tasks;

        ClientData(SocketChannel socketChannel) throws IOException {
            this.socketChannel = socketChannel;
            this.socketChannel.configureBlocking(false);
            this.socketChannel.register(receivingSelector, socketChannel.validOps());
            this.socketChannel.register(sendingSelector, socketChannel.validOps());
            isReady = false;
            tasks = x;
        }

        public void close() throws IOException {
            tasks--;
            if (tasks == 0) {
                clients.remove(socketChannel);
                socketChannel.close();
            }
        }

        public void receiveFromClient() throws IOException {
            ByteBuffer receivingHeader = ByteBuffer.allocate(Integer.BYTES);
            int receivedBytes;
            int totalReceivedBytes = 0;
            do {
                receivedBytes = socketChannel.read(receivingHeader);
                totalReceivedBytes += receivedBytes;
            } while (receivedBytes > 0);
            receivingHeader.flip();
            int receivingSize = receivingHeader.getInt();
            ByteBuffer receivingSource = ByteBuffer.allocate(receivingSize);
            do {
                receivedBytes = socketChannel.read(receivingSource);
            } while (receivedBytes > 0);
            receivingSource.flip();
            List<Integer> numbers = Numbers.parseFrom(receivingSource.array()).getNumbersList();
            threadPool.submit(() -> {
                    setData(Sort.bubbleSort(numbers.stream().mapToInt(Integer::intValue).toArray()));
                    isReady = true;
            });
        }

        public void setData(int[] data) {
            this.data = data;
        }

        public void sendToClient() {
            try {
                Numbers.Builder numbersBuilder = Numbers.newBuilder()
                        .addAllNumbers(Arrays.stream(data).boxed().collect(Collectors.toList()));
                numbersBuilder.setSize(data.length);
                Numbers numbers = numbersBuilder.build();
                ByteBuffer source = ByteBuffer.allocate(Integer.BYTES + numbers.getSerializedSize());
                source.putInt(numbers.getSerializedSize());
                source.put(numbers.toByteArray());
                source.flip();
                int sendingSize = 0;
                int curSending;
                while (source.hasRemaining()) {
                    curSending = socketChannel.write(source);
                    sendingSize += curSending;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
