package ru.hse.java;

import ru.hse.java.numbers.protos.Numbers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingClient extends Client {

    public BlockingClient(int id, int n, int x, int delta) {
        super(id, n, x, delta);
    }

    @Override
    public Void call() {
        try (Socket socket = new Socket(Constants.LOCALHOST, Constants.PORT)) {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());
            for (int i = 0; i < x; i++) {
                Numbers data = generateData();
                data.writeDelimitedTo(os);
                System.out.println("Client #" + id + " send #" + i + " data to server");

                Numbers sortedData = Numbers.parseDelimitedFrom(is);
                checkSorting(sortedData, i);
                Thread.sleep(delta);
            }
        } catch (IOException e) {
            System.out.println("Lost connection to server");
        } catch (InterruptedException ignored) {
        }
        return null;
    }
}
