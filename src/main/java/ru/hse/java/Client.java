package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Client implements Callable<Void> {

    protected final int id;
    protected final int n; // quantity of arrays' elements
    protected final int x;
    protected final int delta;

    protected Client(int id, int n, int x, int delta) {
        this.id = id;
        this.n = n;
        this.x = x;
        this.delta = delta;
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

    @Override
    public abstract Void call();

    protected void checkSorting(Numbers numbers, int k) {
        List<Integer> list = numbers.getNumbersList();
        if (list.size() != n) {
            System.out.println("No all data #" + k + " in client #" + id);
            return;
        }
        for (int i = 0; i < n - 1; i++) {
            if (list.get(i) > list.get(i + 1)) {
                System.out.println("Not sorted data #" + k + " in client #" + id);
                return;
            }
        }
        System.out.println("Client #" + id + " successfully received #" + k + " data");
    }
}
