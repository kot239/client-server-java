package ru.hse.java;

import ru.hse.java.clients.AsynchronousClient;
import ru.hse.java.clients.BlockingClient;
import ru.hse.java.clients.NonBlockingClient;
import ru.hse.java.servers.AsynchronousServer;
import ru.hse.java.servers.BlockingServer;
import ru.hse.java.servers.NonBlockingServer;
import ru.hse.java.servers.Server;
import ru.hse.java.utils.ClientNumbers;
import ru.hse.java.utils.LogCSVWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static int n = 20; // size of array
    private static int m = 20; // number of clients
    private static int delta = 200;
    private static int x = 2;
    private static int numberOfThreads = 5;
    private static int startParam;
    private static int endParam;
    private static int stepParam;
    private static Architectures architecture;
    private static Parameter parameter;
    private static boolean measuringTime; // true if client
    private static final Scanner scanner = new Scanner(System.in);

    private enum Architectures {
        BLOCKING("Blocking architecture", 1),
        NON_BLOCKING("Non blocking architecture", 2),
        ASYNCHRONOUS("Asynchronous architecture", 3);

        private final String name;
        private final int number;
        Architectures(String name, int number) {
            this.name = name;
            this.number = number;
        }
    }

    private enum Parameter {
        ARRAY("number of elements in the array", 1),
        CLIENTS("number of clients", 2),
        TIME("time between receiving and sending message", 3);

        private final String name;
        private final int number;
        Parameter(String name, int number) {
            this.name = name;
            this.number = number;
        }
    }

    private static void chooseArchitecture() {
        System.out.println("Choose type of server architecture:");
        System.out.println("1) " + Architectures.BLOCKING.name);
        System.out.println("2) " + Architectures.NON_BLOCKING.name);
        System.out.println("3) " + Architectures.ASYNCHRONOUS.name);
        System.out.println("Write number of architecture");
        while (true) {
            System.out.print(">> ");
            int num = scanner.nextInt();
            if (num == Architectures.BLOCKING.number) {
                architecture = Architectures.BLOCKING;
                break;
            } else if (num == Architectures.NON_BLOCKING.number) {
                architecture = Architectures.NON_BLOCKING;
                break;
            } else if (num == Architectures.ASYNCHRONOUS.number) {
                architecture = Architectures.ASYNCHRONOUS;
                break;
            } else {
                System.out.println("You write wrong number. Try again");
            }
        }
        System.out.println("You choose " + architecture.name);
    }

    private static void chooseNumberOfTasks() {
        System.out.println("Choose number of tasks for one client:");
        System.out.print(">> ");
        x = scanner.nextInt();
        System.out.println("You choose x = " + x + " number of tasks");
    }

    private static void chooseParameter() {
        System.out.println("Choose what you want to change:");
        System.out.println("1) " + Parameter.ARRAY.name);
        System.out.println("2) " + Parameter.CLIENTS.name);
        System.out.println("3) " + Parameter.TIME.name);
        System.out.println("Write number of parameter:");
        while (true) {
            System.out.print(">> ");
            int num = scanner.nextInt();
            if (num == Parameter.ARRAY.number) {
                parameter = Parameter.ARRAY;
                break;
            } else if (num == Parameter.CLIENTS.number) {
                parameter = Parameter.CLIENTS;
                break;
            } else if (num == Parameter.TIME.number) {
                parameter = Parameter.TIME;
                break;
            } else {
                System.out.println("You write wrong number. Try again");
            }
        }
        System.out.println("You choose " + parameter.name);
        System.out.println("Set start value of parameter:");
        System.out.print(">> ");
        startParam = scanner.nextInt();
        System.out.println("Set end value of parameter (it must be more than start value!):");
        System.out.print(">> ");
        endParam = scanner.nextInt();
        while (endParam < startParam) {
            System.out.println("End value must be more then start value!");
            System.out.print(">> ");
            endParam = scanner.nextInt();
        }
        System.out.println("Set step value of parameter:");
        System.out.print(">> ");
        stepParam = scanner.nextInt();
        System.out.println("You choose:");
        System.out.println("Start value: " + startParam);
        System.out.println("End value: " + endParam);
        System.out.println("Step: " + stepParam);
    }

    private static void chooseOtherParameters() {
        for (Parameter param: Parameter.values()) {
            if (!parameter.equals(param)) {
                System.out.println("Choose " + param.name + ":");
                System.out.print(">> ");
                switch (param) {
                    case ARRAY:
                        n = scanner.nextInt();
                        System.out.println("You choose n = " + n + " " + param.name);
                        break;
                    case CLIENTS:
                        m = scanner.nextInt();
                        System.out.println("You choose m = " + m + " " + param.name);
                        break;
                    case TIME:
                        delta = scanner.nextInt();
                        System.out.println("You choose delta = " + delta + " " + param.name);
                        break;
                }
            }
        }
    }

    private static void chooseMetricType() {
        System.out.println("Choose type of measuring time:");
        System.out.println("1) from sending message by client to receiving it back");
        System.out.println("2) from receiving message by server to sending it back");
        System.out.println("Write number of type:");
        while (true) {
            System.out.print(">> ");
            int num = scanner.nextInt();
            if (num == 1) {
                measuringTime = true;
                System.out.println("You choose measure time from sending message by client to receiving it back");
                break;
            } else if (num == 2) {
                measuringTime = false;
                System.out.println("You choose measure time from receiving message by server to sending it back");
                break;
            } else {
                System.out.println("You write wrong number. Try again");
            }
        }
    }

    private static String getCSVFilename() {
        String csvFilename = "";
        switch (architecture) {
            case BLOCKING:
                csvFilename += "block_";
                break;
            case NON_BLOCKING:
                csvFilename += "nblock_";
                break;
            case ASYNCHRONOUS:
                csvFilename += "async_";
                break;
        }
        switch (parameter) {
            case ARRAY:
                csvFilename += "n_";
                break;
            case CLIENTS:
                csvFilename += "m_";
                break;
            case TIME:
                csvFilename += "d_";
                break;
        }
        csvFilename += startParam;
        csvFilename += "_";
        csvFilename += endParam;
        csvFilename += "_";
        csvFilename += stepParam;
        csvFilename += "_";
        csvFilename += x;
        csvFilename += "_";
        csvFilename += n;
        csvFilename += "_";
        csvFilename += m;
        csvFilename += "_";
        csvFilename += delta;
        csvFilename += "_";
        csvFilename += (measuringTime ? "c.csv" : "s.csv");
        return csvFilename;
    }

    public static void main(String[] args) {
        chooseArchitecture();
        chooseNumberOfTasks();
        chooseParameter();
        chooseOtherParameters();
        chooseMetricType();
        String csvFilename = getCSVFilename();
        ClientNumbers clients = new ClientNumbers();
        try {
            Path csvPath = LogCSVWriter.createCSVFile(csvFilename);
            StringJoiner joiner = new StringJoiner(",");
            System.out.println("Start program");
            for (int paramValue = startParam; paramValue <= endParam; paramValue += stepParam) {
                switch (parameter) {
                    case ARRAY:
                        n = paramValue;
                        break;
                    case CLIENTS:
                        m = paramValue;
                        break;
                    case TIME:
                        delta = paramValue;
                        break;
                }
                Server server;
                switch (architecture) {
                    case BLOCKING:
                        server = new BlockingServer(m, numberOfThreads, clients);
                        break;
                    case NON_BLOCKING:
                        server = new NonBlockingServer(x, m, numberOfThreads, clients);
                        break;
                    default:
                        server = new AsynchronousServer(x, m, numberOfThreads, clients);
                        break;
                }
                server.run();
                ExecutorService clientsThreadPool = Executors.newCachedThreadPool();
                List<Future<Double>> futures = clientsThreadPool.invokeAll(
                        IntStream.range(0, m)
                                .mapToObj(i ->  {
                                    switch (architecture) {
                                        case BLOCKING:
                                            return new BlockingClient(i + 1, m, n, x, delta, clients);
                                        case NON_BLOCKING:
                                            return new NonBlockingClient(i + 1, m, n, x, delta, clients);
                                        default:
                                            return new AsynchronousClient(i + 1, m, n, x, delta, clients);
                                    }
                                })
                                .collect(Collectors.toList())
                );
                List<Double> clientsTimes = new LinkedList<>();
                for (Future<Double> future : futures) {
                    clientsTimes.add(future.get());
                }
                clientsThreadPool.shutdown();
                double clientsTime = clientsTimes
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .filter(x -> x != -1d)
                        .sum() / clientsTimes.stream()
                        .mapToDouble(Double::doubleValue)
                        .filter(x -> x != -1d)
                        .count();
                double serverTime = server.close();
                joiner.add(Double.toString(measuringTime ? clientsTime : serverTime));
                System.out.println("Do param: " + paramValue);
            }
            LogCSVWriter.writeToFile(csvPath, joiner.toString());
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
