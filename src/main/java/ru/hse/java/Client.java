package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
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

    protected Client(int id, int m, int n, int x, int delta, String fileName, ClientNumbers clientNumbers) {
        this.id = id;
        this.m = m;
        this.n = n;
        this.x = x;
        this.delta = delta;
        this.logPath = LogWriter.createLogFile(fileName);
        this.clientNumbers = clientNumbers;
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
            LogWriter.writeToLog(logPath, "No all data #" + k + " in client #" + id + '\n');
            return;
        }
        for (int i = 0; i < n - 1; i++) {
            if (list.get(i) > list.get(i + 1)) {
                LogWriter.writeToLog(logPath, "Not sorted data #" + k + " in client #" + id + '\n');
                return;
            }
        }
        LogWriter.writeToLog(logPath, "Client #" + id + " successfully received #" + k + " data\n");
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
