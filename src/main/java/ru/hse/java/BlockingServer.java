package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BlockingServer {

    private int[] bubbleSort(int[] input) {
        int n = input.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (input[j] > input[j + 1]) {
                    int t = input[j];
                    input[j] = input[j + 1];
                    input[j + 1] = t;
                }
            }
        }
        return input;
    }

    private final ExecutorService serverSocketService = Executors.newSingleThreadExecutor();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2);;
    private ServerSocket serverSocket;

    private volatile boolean isWorking = true;

    private final ConcurrentHashMap.KeySetView<ClientData, Boolean> clients = ConcurrentHashMap.newKeySet();

    public void run() throws IOException {
        serverSocket = new ServerSocket(Constants.PORT);
        serverSocketService.submit(this::acceptClient);
    }

    public void stop() throws IOException {
        isWorking = false;
        serverSocket.close();
        threadPool.shutdown();
        serverSocketService.shutdown();
        clients.forEach(ClientData::close);
    }

    public void acceptClient() {
        try (ServerSocket ignored = serverSocket) {
            while (isWorking) {
                Socket socket = serverSocket.accept();
                System.out.println("client accepted");
                ClientData client = new ClientData(socket);
                clients.add(client);
                client.receiveFromClient();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientData {
        private final ExecutorService receivingThread = Executors.newSingleThreadExecutor();
        private final ExecutorService sendingThread = Executors.newSingleThreadExecutor();

        private final Socket socket;

        private final DataInputStream is;
        private final DataOutputStream os;

        private boolean isReceiving;

        private ClientData(Socket socket) throws IOException {
            this.socket = socket;
            isReceiving = true;
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        }

        public void receiveFromClient() {
            receivingThread.submit(() -> {
                try {
                    while (isReceiving) {
                        List<Integer> numbers = Numbers.parseDelimitedFrom(is).getNumbersList();
                        threadPool.submit(() ->
                                sendToClient(bubbleSort(numbers.stream().mapToInt(Integer::intValue).toArray()))
                        );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public void sendToClient(int[] data) {
            sendingThread.submit(() -> {
                try {
                    Numbers.Builder numbers = Numbers.newBuilder()
                            .addAllNumbers(Arrays.stream(data).boxed().collect(Collectors.toList()));
                    numbers.setSize(data.length);
                    numbers.build().writeDelimitedTo(os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public void close() {
            isReceiving = false;
            receivingThread.shutdown();
            sendingThread.shutdown();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
