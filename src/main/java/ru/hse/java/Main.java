package ru.hse.java;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final int m = 20; // number of clients
    private static final int n = 20; // size of array
    private static final int x = 2;
    private static final int delta = 200;

    public static void main(String[] args) {
        ClientNumbers clients = new ClientNumbers();
        try {
            Server server = new AsynchronousServer(m, clients);
            server.run();
            ExecutorService clientsThreadPool = Executors.newCachedThreadPool();
            List<Future<Double>> futures = clientsThreadPool.invokeAll(
                    IntStream.range(0, m)
                            .mapToObj(i -> new AsynchronousClient(i + 1, m, n, x, delta, clients))
                            .collect(Collectors.toList())
            );
            List<Double> clientsTimes = new LinkedList<>();
            for (Future<Double> future : futures) {
                clientsTimes.add(future.get());
            }
            clientsThreadPool.shutdown();
            double clientsTime = clientsTimes.stream().mapToDouble(Double::doubleValue).sum() / clientsTimes.size();
            System.out.println(clientsTime);
            System.out.println(server.close());
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
