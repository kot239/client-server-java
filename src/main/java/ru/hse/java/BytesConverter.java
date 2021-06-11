package ru.hse.java;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BytesConverter {
    private static final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);

    public static byte[] concat(byte[] array1, byte[] array2) {
        byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public static byte[] intToBytes(int x) {
        buffer.putInt(0, x);
        return buffer.array();
    }

    public static int bytesToInt(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getInt();
    }
}
