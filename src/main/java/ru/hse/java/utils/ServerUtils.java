package ru.hse.java.utils;

import ru.hse.java.numbers.protos.Numbers;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ServerUtils {
    public static int[] bubbleSort(int[] input) {
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

    public static ByteBuffer arrayToByteBuffer(int[] data) {
        Numbers.Builder numbersBuilder = Numbers.newBuilder()
                .addAllNumbers(Arrays.stream(data).boxed().collect(Collectors.toList()));
        numbersBuilder.setSize(data.length);
        Numbers numbersOut = numbersBuilder.build();
        ByteBuffer source = ByteBuffer.allocate(Integer.BYTES + numbersOut.getSerializedSize());
        source.putInt(numbersOut.getSerializedSize());
        source.put(numbersOut.toByteArray());
        source.flip();
        return source;
    }
}
