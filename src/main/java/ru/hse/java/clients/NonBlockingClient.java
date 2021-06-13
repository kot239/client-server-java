package ru.hse.java.clients;

import ru.hse.java.utils.ClientNumbers;
import ru.hse.java.utils.Constants;
import ru.hse.java.utils.LogCSVWriter;
import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NonBlockingClient extends Client {

    public NonBlockingClient(int id, int m, int n, int x, int delta, ClientNumbers clientNumbers) {
        super(id, m, n, x, delta, "NonBlockingClientLog.txt", clientNumbers);
    }

    @Override
    public Double call() {
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT))) {
            for (int i = 0; i < x; i++) {
                ByteBuffer source = prepareSource();
                while (source.hasRemaining()) {
                    channel.write(source);
                }
                LogCSVWriter.writeToFile(logPath, "Client #" + id + " send #" + i + " data to server\n");
                long startTime = System.currentTimeMillis();

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
                if (m == clientNumbers.getNumber()) {
                    times.add(System.currentTimeMillis() - startTime);
                }
                checkSorting(Numbers.parseFrom(receivingSource.array()), i);

                Thread.sleep(delta);
            }
            return returnClientTime();
        } catch (IOException e) {
            LogCSVWriter.writeToFile(logPath, "Lost connection to server\n");
        } catch (InterruptedException ignored) {
        }
        return -1d;
    }
}
