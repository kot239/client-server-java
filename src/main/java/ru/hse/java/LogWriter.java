package ru.hse.java;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LogWriter {

    private static final Path logDir = Paths.get("src/main/java/log/");

    public static Path createLogFile(String fileName) {
        try {
            if (!Files.exists(logDir)) {
                Files.createDirectory(logDir);
            }
            Path filePath = logDir.resolve(fileName);
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

    public static void writeToLog(Path logPath, String msg) {
        try {
            Files.write(logPath, msg.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Can't write log in file");
        }
    }
}
