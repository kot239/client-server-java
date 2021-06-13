package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BlockingServer extends Server {

    private final Path logPath = LogWriter.createLogFile("BlockingServerLog.txt");

    private final ExecutorService serverSocketService = Executors.newSingleThreadExecutor();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2);;
    private ServerSocket serverSocket;

    private volatile boolean isWorking = false;

    private final ConcurrentHashMap.KeySetView<ClientData, Boolean> clients = ConcurrentHashMap.newKeySet();

    BlockingServer(int m, ClientNumbers clientNumbers) {
        super(m, clientNumbers);
    }

    @Override
    public void run() throws IOException {
        isWorking = true;
        serverSocket = new ServerSocket(Constants.PORT);
        serverSocketService.submit(this::acceptClient);
    }

    @Override
    public double close() throws IOException {
        isWorking = false;
        serverSocket.close();
        threadPool.shutdown();
        serverSocketService.shutdown();
        clients.forEach(ClientData::close);
        return returnServerTime();
    }

    public void acceptClient() {
        try (ServerSocket ignored = serverSocket) {
            while (isWorking) {
                Socket socket = serverSocket.accept();
                LogWriter.writeToLog(logPath, "client accepted\n");
                clientNumbers.incClients();
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

        private long startTime;

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
                        startTime = System.currentTimeMillis();
                        LogWriter.writeToLog(logPath, "receive data from client\n");
                        threadPool.submit(() ->
                                sendToClient(ServerUtils.bubbleSort(numbers.stream().mapToInt(Integer::intValue).toArray()))
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
                    addTimes(startTime);
                    LogWriter.writeToLog(logPath, "send data to client\n");
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
                clientNumbers.decClients();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
