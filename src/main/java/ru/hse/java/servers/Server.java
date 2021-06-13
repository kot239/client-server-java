package ru.hse.java.servers;

import ru.hse.java.utils.ClientNumbers;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class Server {

    protected int m;
    protected final ClientNumbers clientNumbers;

    protected final ConcurrentLinkedDeque<Long> times = new ConcurrentLinkedDeque<>();

    Server(int m, ClientNumbers clientNumbers) {
        this.m = m;
        this.clientNumbers = clientNumbers;
    }

    protected double returnServerTime() {
        double n = times.size();
        return (double) times.stream().mapToLong(Long::longValue).sum() / n;
    }

    protected synchronized void addTimes(long startTime) {
        int curClients = clientNumbers.getNumber();
        if (curClients == m) {
            long endTime = System.currentTimeMillis() - startTime;
            times.add(endTime);
        }
    }

    public abstract void run() throws IOException;

    public abstract double close() throws IOException;
}
