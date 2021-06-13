package ru.hse.java;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousServer {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2);
    private final AsynchronousChannelGroup group;
    private AsynchronousServerSocketChannel serverSocketChannel;

    private final int x = 2;

    public AsynchronousServer() throws IOException {
        ExecutorService serverThread = Executors.newSingleThreadExecutor();
        group = AsynchronousChannelGroup.withThreadPool(serverThread);
    }

    public void run() throws IOException {
        serverSocketChannel = AsynchronousServerSocketChannel.open(group);
        serverSocketChannel.bind(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT));
        accept();
    }

    public void close() {
        group.shutdown();
        threadPool.shutdown();
    }

    public void accept() throws IOException {
        while (true) {
            serverSocketChannel.accept(null, new CompletionHandler<>() {
                @Override
                public void completed(AsynchronousSocketChannel channel, Object attachment) {
                    if (serverSocketChannel.isOpen()) {
                        serverSocketChannel.accept(null, this);
                    }

                    if ((channel != null) && channel.isOpen()) {
                        ClientHandler handler = new ClientHandler(channel);

                        Info info = new Info();
                        System.out.println("accept client");
                        channel.read(info.header, info, handler);
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                }
            });
            System.in.read();
        }
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
                        //System.out.println(info.sourceSize);

                        channel.read(info.source, info, this);
                    } else { // we read not enough
                        System.out.println(result);
                        channel.read(info.header, info, this);
                    }
                } else { // read source
                    info.sourceSize -= result;
                    if (info.sourceSize == 0) {
                        //info.source.flip();

                        try {
                            List<Integer> numbers = Numbers.parseFrom(info.source.array()).getNumbersList();
                            threadPool.submit(() -> {
                                data = ServerUtils.bubbleSort(numbers.stream().mapToInt(Integer::intValue).toArray());
                                info.source = ServerUtils.arrayToByteBuffer(data);
                                info.action = "write";
                                info.sourceSize = info.source.capacity();
                                System.out.println("read data from client");

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
                    System.out.println("send data to client");
                    info.tasks--;
                    if (info.tasks == 0) {
                        try {
                            channel.close();
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
    }
}
