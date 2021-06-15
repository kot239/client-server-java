package ru.hse.java.clients;

import ru.hse.java.utils.ClientNumbers;
import ru.hse.java.utils.LogCSVWriter;
import ru.hse.java.numbers.protos.Numbers;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Client implements Callable<Double> {

    protected final int id;
    protected final int m;
    protected final int n; // quantity of arrays' elements
    protected final int x;
    protected final int delta;
    protected final Path logPath;
    protected final List<Long> times = new LinkedList<>();
    protected final ClientNumbers clientNumbers;
    protected final CountDownLatch latch;
    protected final ExecutorService sendingThread = Executors.newSingleThreadExecutor();
    protected final ExecutorService receivingThread = Executors.newSingleThreadExecutor();

    protected Client(int id, int m, int n, int x, int delta,
                     String fileName, ClientNumbers clientNumbers, CountDownLatch latch) {
        this.id = id;
        this.m = m;
        this.n = n;
        this.x = x;
        this.delta = delta;
        this.logPath = LogCSVWriter.createLogFile(fileName);
        this.clientNumbers = clientNumbers;
        this.latch = latch;
    }

    protected Numbers generateData() {
        Random r = new Random();
        List<Integer> list = IntStream.generate(r::nextInt)
                .limit(n)
                .boxed()
                .collect(Collectors.toList());
        Numbers.Builder numbers = Numbers.newBuilder();
        numbers.setSize(n);
        numbers.addAllNumbers(list);
        return numbers.build();
    }

    protected double returnClientTime() {
        double n = times.size();
        return (double) times.stream().mapToLong(Long::longValue).sum() / n;
    }

    @Override
    public abstract Double call();

    protected void checkSorting(Numbers numbers, int k) {
        List<Integer> list = numbers.getNumbersList();
        if (list.size() != n) {
            LogCSVWriter.writeToFile(logPath, "No all data #" + k + " in client #" + id + '\n');
            return;
        }
        for (int i = 0; i < n - 1; i++) {
            if (list.get(i) > list.get(i + 1)) {
                LogCSVWriter.writeToFile(logPath, "Not sorted data #" + k + " in client #" + id + '\n');
                return;
            }
        }
        LogCSVWriter.writeToFile(logPath, "Client #" + id + " successfully received #" + k + " data\n");
    }

    protected ByteBuffer prepareSource() {
        Numbers numbers = generateData();
        ByteBuffer source = ByteBuffer.allocate(Integer.BYTES + numbers.getSerializedSize());
        source.putInt(numbers.getSerializedSize());
        source.put(numbers.toByteArray());
        source.flip();
        return source;
    }
}
