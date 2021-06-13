package ru.hse.java.servers;

import ru.hse.java.utils.ClientNumbers;
import ru.hse.java.utils.Constants;
import ru.hse.java.utils.LogCSVWriter;
import ru.hse.java.utils.ServerUtils;
import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NonBlockingServer extends Server {

    private final Path logPath = LogCSVWriter.createLogFile("NonBlockingServerLog.txt");

    private final ExecutorService receivingThread = Executors.newSingleThreadExecutor();
    private final ExecutorService sendingThread = Executors.newSingleThreadExecutor();
    private final ExecutorService threadPool;

    private volatile boolean isWorking = false;

    private ServerSocketChannel serverSocketChannel;

    private final ConcurrentHashMap<SocketChannel, ClientData> clients = new ConcurrentHashMap<>();

    private Selector receivingSelector;
    private Selector sendingSelector;

    private final int x;

    public NonBlockingServer(int x, int m, int numberOfThreads, ClientNumbers clientNumbers) {
        super(m, clientNumbers);
        this.x = x;
        threadPool = Executors.newFixedThreadPool(numberOfThreads);
    }

    @Override
    public void run() throws IOException {
        isWorking = true;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT));
        receivingThread.submit(this::receiveMessages);
        sendingThread.submit(this::sendMessages);
    }

    @Override
    public double close() throws IOException {
        isWorking = false;
        receivingSelector.close();
        sendingSelector.close();
        serverSocketChannel.close();
        //receivingThread.shutdown();
        //sendingThread.shutdown();
        return returnServerTime();
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
                        LogCSVWriter.writeToFile(logPath, "client accepted\n");
                        clientNumbers.incClients();
                    }
                    if (key.isReadable()) {
                        ClientData client = clients.get((SocketChannel) key.channel());
                        client.receiveFromClient();
                        LogCSVWriter.writeToFile(logPath, "receive data from client\n");
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
                //System.out.println("Work bitch");
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
                            clientNumbers.decClients();
                            LogCSVWriter.writeToFile(logPath, "send data to client\n");
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

        private long startTime;

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
            do {
                receivedBytes = socketChannel.read(receivingHeader);
            } while (receivedBytes > 0);
            receivingHeader.flip();
            int receivingSize = receivingHeader.getInt();
            ByteBuffer receivingSource = ByteBuffer.allocate(receivingSize);
            do {
                receivedBytes = socketChannel.read(receivingSource);
            } while (receivedBytes > 0);
            receivingSource.flip();
            startTime = System.currentTimeMillis();
            List<Integer> numbers = Numbers.parseFrom(receivingSource.array()).getNumbersList();
            threadPool.submit(() -> {
                    setData(ServerUtils.bubbleSort(numbers.stream().mapToInt(Integer::intValue).toArray()));
                    isReady = true;
            });
        }

        public void setData(int[] data) {
            this.data = data;
        }

        public void sendToClient() {
            try {
                ByteBuffer source = ServerUtils.arrayToByteBuffer(data);
                while (source.hasRemaining()) {
                    socketChannel.write(source);
                }
                addTimes(startTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}