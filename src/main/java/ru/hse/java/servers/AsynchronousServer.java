package ru.hse.java.servers;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.hse.java.utils.ClientNumbers;
import ru.hse.java.utils.Constants;
import ru.hse.java.utils.LogCSVWriter;
import ru.hse.java.utils.ServerUtils;
import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousServer extends Server {

    private final Path logPath = LogCSVWriter.createLogFile("AsynchronousServerLog.txt");

    private final ExecutorService threadPool;
    private final AsynchronousChannelGroup group;
    private AsynchronousServerSocketChannel serverSocketChannel;

    private final int x;

    public AsynchronousServer(int x, int m, int numberOfThreads, ClientNumbers clientNumbers) throws IOException {
        super(m, clientNumbers);
        this.x = x;
        threadPool = Executors.newFixedThreadPool(numberOfThreads);
        ExecutorService serverThread = Executors.newSingleThreadExecutor();
        group = AsynchronousChannelGroup.withThreadPool(serverThread);
    }

    @Override
    public void run() throws IOException {
        clientNumbers.setZero();
        serverSocketChannel = AsynchronousServerSocketChannel.open(group);
        serverSocketChannel.bind(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT));
        accept();
    }

    @Override
    public double close() throws IOException {
        serverSocketChannel.close();
        group.shutdown();
        threadPool.shutdown();
        return returnServerTime();
    }

    public void accept() throws IOException {
        //while (true) {
            serverSocketChannel.accept(null, new CompletionHandler<>() {
                @Override
                public void completed(AsynchronousSocketChannel channel, Object attachment) {
                    if (serverSocketChannel.isOpen()) {
                        serverSocketChannel.accept(null, this);
                    }

                    if ((channel != null) && channel.isOpen()) {
                        ClientHandler handler = new ClientHandler(channel);

                        Info info = new Info();
                        LogCSVWriter.writeToFile(logPath, "client accepted\n");
                        clientNumbers.incClients();
                        channel.read(info.header, info, handler);
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                }
            });
            //System.in.read();
        //}
    }

    private class ClientHandler implements CompletionHandler<Integer, Info> {

        private final AsynchronousSocketChannel channel;
        private int[] data;

        ClientHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, Info info) {

            if (info.action.equals("read")) {
                if (info.source == null) { // read header
                    info.headerSize += result;
                    if (info.headerSize == Integer.BYTES) { // we want to read source
                        info.header.flip();
                        info.sourceSize = info.header.getInt();
                        info.source = ByteBuffer.allocate(info.sourceSize);

                        channel.read(info.source, info, this);
                    } else { // we read not enough
                        System.out.println(result);
                        channel.read(info.header, info, this);
                    }
                } else { // read source
                    info.sourceSize -= result;
                    if (info.sourceSize == 0) {
                        info.startTime = System.currentTimeMillis();
                        try {
                            List<Integer> numbers = Numbers.parseFrom(info.source.array()).getNumbersList();
                            threadPool.submit(() -> {
                                data = ServerUtils.bubbleSort(numbers.stream().mapToInt(Integer::intValue).toArray());
                                info.source = ServerUtils.arrayToByteBuffer(data);
                                info.action = "write";
                                info.sourceSize = info.source.capacity();
                                LogCSVWriter.writeToFile(logPath, "read data from client\n");

                                channel.write(info.source, info, this);
                            });
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    } else {
                        channel.read(info.source, info, this);
                    }
                }
            } else if (info.action.equals("write")) {
                info.sourceSize -= result;
                if (info.sourceSize == 0) { // end of write
                    LogCSVWriter.writeToFile(logPath, "send data to client\n");
                    addTimes(info.startTime);
                    info.tasks--;
                    if (info.tasks == 0) {
                        try {
                            channel.close();
                            clientNumbers.decClients();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    info.action = "read";
                    info.header.clear();
                    info.headerSize = 0;
                    info.source = null;

                    channel.read(info.header, info, this);
                } else {
                    channel.write(info.source, info, this);
                }
            }
        }

        @Override
        public void failed(Throwable exc, Info attachment) {
        }
    }

    private class Info {
        String action = "read";
        ByteBuffer header = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer source = null;
        int headerSize = 0;
        int sourceSize = 0;
        int tasks = x;
        long startTime;
    }
}
