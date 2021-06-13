package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchronousClient extends Client {

    public AsynchronousClient(int id, int n, int x, int delta) {
        super(id, n, x, delta, "AsynchronousClientLog.txt");
    }

    @Override
    public Void call() {
        try (AsynchronousSocketChannel channel = AsynchronousSocketChannel.open()) {
            Future<Void> future = channel.connect(new InetSocketAddress(Constants.LOCALHOST, Constants.PORT));
            future.get();
            for (int i = 0; i < x; i++) {
                ByteBuffer source = prepareSource();
                while (source.hasRemaining()) {
                    channel.write(source);
                }
                LogWriter.writeToLog(logPath,"Client #" + id + " send #" + i + " data to server\n");

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
                checkSorting(Numbers.parseFrom(receivingSource.array()), i);

                Thread.sleep(delta);
            }
        } catch (IOException e) {
            LogWriter.writeToLog(logPath, "Lost connection to server\n");
        } catch (InterruptedException ignored) {
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
