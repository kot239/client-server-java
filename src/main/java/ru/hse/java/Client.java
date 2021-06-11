package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Client implements Callable<Void> {

    private final int id;
    private final int n; // quantity of arrays' elements

    public Client(int id, int n) {
        this.id = id;
        this.n = n;
    }

    private Numbers generateData() {
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
    public Void call() {
        Numbers data = generateData();
        try (Socket socket = new Socket(Constants.LOCALHOST, Constants.PORT)) {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            data.writeDelimitedTo(os);
            System.out.println("Client #" + id + " send data to server");

            DataInputStream is = new DataInputStream(socket.getInputStream());
            Numbers sortedData = Numbers.parseDelimitedFrom(is);
            checkSorting(sortedData);
        } catch (IOException e) {
            System.out.println("Lost connection to server");
        }
        return null;
    }

    private void checkSorting(Numbers numbers) {
        List<Integer> list = numbers.getNumbersList();
        if (list.size() != n) {
            System.out.println("No all data in client #" + id);
            return;
        }
        for (int i = 0; i < n - 1; i++) {
            if (list.get(i) > list.get(i + 1)) {
                System.out.println("Not sorted data in client #" + id);
                return;
            }
        }
        System.out.println("Client #" + id + " successfully received data");
    }
}
