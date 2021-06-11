package ru.hse.java;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClientsMain {
    private static final int m = 20;
    private static final int n = 20;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService clientsThreadPool = Executors.newCachedThreadPool();
        List<Future<Void>> futures = clientsThreadPool.invokeAll(
                IntStream.range(0, m).mapToObj(i -> new Client(i + 1, n)).collect(Collectors.toList())
        );
        for (Future<Void> future : futures) {
            future.get();
        }
        clientsThreadPool.shutdown();

    }
}
