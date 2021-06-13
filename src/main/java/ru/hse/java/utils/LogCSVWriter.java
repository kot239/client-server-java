package ru.hse.java.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LogCSVWriter {

    private static final Path logDir = Paths.get("src/main/java/log/");
    private static final Path csvDir = Paths.get("src/main/java/csv/");

    private static Path createFile(Path dir, String fileName) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }
            Path filePath = dir.resolve(fileName);
            if (Files.exists(filePath)) {
                Files.write(filePath, "".getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                Files.createFile(filePath);
            }
            return filePath;
        } catch (IOException e) {
            System.out.println("There is a problem to create log");
        }
        return null;
    }

    public static Path createLogFile(String fileName) {
        return createFile(logDir, fileName);
    }

    public static Path createCSVFile(String fileName) {
        return createFile(csvDir, fileName);
    }

    public static void writeToFile(Path logPath, String msg) {
        try {
            Files.write(logPath, msg.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Can't write log in file");
        }
    }
}
