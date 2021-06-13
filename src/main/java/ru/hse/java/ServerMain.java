package ru.hse.java;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        try {
            AsynchronousServer server = new AsynchronousServer();
            server.run();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                //System.out.print(">> ");
                String curLine = scanner.nextLine();
                if (curLine.equals("exit")) {
                    break;
                }
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
