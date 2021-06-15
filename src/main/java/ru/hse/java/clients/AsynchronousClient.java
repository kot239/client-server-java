package ru.hse.java.clients;

import ru.hse.java.utils.ClientNumbers;
import ru.hse.java.utils.Constants;
import ru.hse.java.utils.LogCSVWriter;
import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchronousClient extends Client {

    public AsynchronousClient(int id, int m, int n, int x, int delta, ClientNumbers clientNumbers, CountDownLatch latch) {
        super(id, m, n, x, delta, "AsynchronousClientLog.txt", clientNumbers, latch);
    }

    @Override
    public Double call() {
        try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open()) {
            Future<Void> future = channel.connect(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT));
            future.get();
            for (int i = 0; i < x; i++) {
                ByteBuffer source = prepareSource();
                while (source.hasRemaining()) {
                    channel.write(source);
                }
                LogCSVWriter.writeToFile(logPath,"Client #" + id + " send #" + i + " data to server\n");
                long startTime = System.currentTimeMillis();

                ByteBuffer receivingHeader = ByteBuffer.allocate(Integer.BYTES);
                Future<Integer> receivedBytes;
                int totalReceivedBytes = Integer.BYTES;
                while (totalReceivedBytes > 0) {
                    receivedBytes = channel.read(receivingHeader);
                    totalReceivedBytes -= receivedBytes.get();
                }
                receivingHeader.flip();
                int receivingSize = receivingHeader.getInt();
                ByteBuffer receivingSource = ByteBuffer.allocate(receivingSize);
                totalReceivedBytes = receivingSize;
                while (totalReceivedBytes > 0) {
                    receivedBytes = channel.read(receivingSource);
                    totalReceivedBytes -= receivedBytes.get();
                }
                if (m == clientNumbers.getNumber()) {
                    times.add(System.currentTimeMillis() - startTime);
                }
                checkSorting(Numbers.parseFrom(receivingSource.array()), i);

                Thread.sleep(delta);
            }
            latch.countDown();
            return returnClientTime();
        } catch (IOException e) {
            LogCSVWriter.writeToFile(logPath, "Lost connection to server\n");
        } catch (InterruptedException ignored) {
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return -1d;
    }
}
