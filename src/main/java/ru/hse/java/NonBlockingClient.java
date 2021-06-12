package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NonBlockingClient extends Client {

    public NonBlockingClient(int id, int n, int x, int delta) {
        super(id, n, x, delta);
    }

    @Override
    public Void call() {
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT))) {
            for (int i = 0; i < x; i++) {
                Numbers numbers = generateData();
                ByteBuffer source = ByteBuffer.allocate(Integer.BYTES + numbers.getSerializedSize());
                source.putInt(numbers.getSerializedSize());
                source.put(numbers.toByteArray());
                source.flip();
                while (source.hasRemaining()) {
                    channel.write(source);
                }
                System.out.println("Client #" + id + " send #" + i + " data to server");

                ByteBuffer receivingHeader = ByteBuffer.allocate(Integer.BYTES);
                int receivedBytes;
                int totalReceivedBytes = Integer.BYTES;
                while (totalReceivedBytes > 0) {
                    receivedBytes = channel.read(receivingHeader);
                    totalReceivedBytes -= receivedBytes;
                }
                receivingHeader.flip();
                int receivingSize = receivingHeader.getInt();
                ByteBuffer receivingSource = ByteBuffer.allocate(receivingSize);
                totalReceivedBytes = receivingSize;
                while (totalReceivedBytes > 0) {
                    receivedBytes = channel.read(receivingSource);
                    totalReceivedBytes -= receivedBytes;
                }
                checkSorting(Numbers.parseFrom(receivingSource.array()), i);

                Thread.sleep(delta);
            }
        } catch (IOException e) {
            System.out.println("Lost connection to server");
        } catch (InterruptedException ignored) {
        }
        return null;
    }
}
