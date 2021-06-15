package ru.hse.java.servers;

import org.apache.tools.ant.taskdefs.Exec;
import ru.hse.java.utils.ClientNumbers;
import ru.hse.java.utils.Constants;
import ru.hse.java.utils.LogCSVWriter;
import ru.hse.java.utils.ServerUtils;
import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NonBlockingServer extends Server {

    private final Path logPath = LogCSVWriter.createLogFile("NonBlockingServerLog.txt");

    private final ExecutorService threadPool;
    private final ExecutorService receivingThread = Executors.newSingleThreadExecutor();
    private final ExecutorService sendingThread = Executors.newSingleThreadExecutor();

    private Selector receivingSelector;
    private Selector sendingSelector;
    private ServerSocketChannel serverSocketChannel;

    private final int x;

    private volatile boolean isWorking;

    private final ConcurrentHashMap<SocketChannel, ClientData> clients = new ConcurrentHashMap<>();

    public NonBlockingServer(int x, int m, int numberOfThreads, ClientNumbers clientNumbers) {
        super(m, clientNumbers);
        threadPool = Executors.newFixedThreadPool(numberOfThreads);
        this.x = x;
    }

    @Override
    public void run() throws IOException {
        clientNumbers.setZero();
        receivingSelector = Selector.open();
        sendingSelector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(receivingSelector, serverSocketChannel.validOps());
        serverSocketChannel.register(sendingSelector, serverSocketChannel.validOps());
        isWorking = true;
        receivingThread.submit(this::receiveMessages);
        sendingThread.submit(this::sendMessages);
    }

    @Override
    public double close() throws IOException {
        isWorking = false;
        receivingSelector.close();
        sendingSelector.close();
        serverSocketChannel.close();
        receivingThread.shutdown();
        sendingThread.shutdown();
        threadPool.shutdown();
        for (ClientData client: clients.values()) {
            client.channel.close();
        }
        return returnServerTime();
    }

    public void receiveMessages() {
        try {
            while (isWorking) {
                receivingSelector.selectNow();
                Set<SelectionKey> keys = receivingSelector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        ClientData client = new ClientData();
                        clients.put(client.channel, client);
                        LogCSVWriter.writeToFile(logPath, "client accepted\n");
                    }
                    if (key.isReadable()) {
                        ClientData client = clients.get((SocketChannel) key.channel());
                        client.receive();
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
            while (isWorking) {
                sendingSelector.selectNow();
                Set<SelectionKey> keys = sendingSelector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isWritable()) {
                        ClientData client = clients.get((SocketChannel) key.channel());
                        if (client.isReady()) {
                            client.send();
                            key.interestOps(0);
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

        private final SocketChannel channel;
        private final ByteBuffer headerBuffer;
        private ByteBuffer sourceBuffer;
        private int headerRead;
        private int sourceRead;
        private int sourceSize;

        private int tasks;

        private long startTime;

        private class Data {
            private final int[] data;

            Data(int[] data) {
                this.data = data;
            }
        }

        ConcurrentLinkedDeque<Data> results = new ConcurrentLinkedDeque<>();

        ClientData() throws IOException {
            channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            channel.register(receivingSelector, SelectionKey.OP_READ);
            headerBuffer = ByteBuffer.allocate(Integer.BYTES);
            headerRead = 0;
            sourceRead = 0;
            sourceSize = -1;
            clientNumbers.incClients();
        }

        public void receive() throws IOException {
            if (headerRead < Integer.BYTES) {
                int bytes = channel.read(headerBuffer);
                headerRead += bytes;
                if (headerRead == Integer.BYTES) {
                    headerBuffer.flip();
                    sourceSize = headerBuffer.getInt();
                    sourceBuffer = ByteBuffer.allocate(sourceSize);
                }
            } else if (sourceSize != -1 && sourceRead < sourceSize) {
                int bytes = channel.read(sourceBuffer);
                sourceRead += bytes;
                if (sourceRead == sourceSize) {
                    List<Integer> numbers = Numbers.parseFrom(sourceBuffer.array()).getNumbersList();
                    LogCSVWriter.writeToFile(logPath, "receive data from client\n");
                    //System.out.println("receive data from client");
                    threadPool.submit(() -> task(numbers));
                    headerRead = 0;
                    sourceRead = 0;
                    sourceSize = -1;
                    headerBuffer.clear();
                    sourceBuffer = null;
                    channel.register(sendingSelector, SelectionKey.OP_WRITE);
                    startTime = System.currentTimeMillis();
                }
            }
        }

        public boolean isReady() {
            return !results.isEmpty();
        }

        public void close() {
            tasks--;
            if (tasks == 0) {
                clientNumbers.decClients();
            }
        }

        public void send() throws IOException {
            Data data = results.remove();
            ByteBuffer source = ServerUtils.arrayToByteBuffer(data.data);
            channel.write(source);
            LogCSVWriter.writeToFile(logPath, "send data to client\n");
            if (clientNumbers.getNumber() == m) {
                addTimes(startTime);
            }
            close();
        }

        public void task(List<Integer> numbers) {
            Data data = new Data(ServerUtils.bubbleSort(numbers
                            .stream()
                            .mapToInt(Integer::intValue)
                            .toArray()));
            results.add(data);
            sendingSelector.wakeup();
        }
    }
}
