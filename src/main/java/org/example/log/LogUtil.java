package org.example.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class LogUtil {
    private final String logPath;
    private final HashMap<LevelLog, Integer> map = new HashMap<>();
    private final Integer logLevel;

    public LogUtil(String logPath, LevelLog logLevel) {
        this.logPath = logPath;
        map.put(LevelLog.Debug, 0);
        map.put(LevelLog.Warning, 1);
        map.put(LevelLog.Error, 2);
        map.put(LevelLog.Fatal, 3);
        this.logLevel = map.get(logLevel);

        Path filePath = Paths.get(logPath, "connector.log");

        try {
            // Проверяем наличие пути к файлу и создаем директории при необходимости
            Files.createDirectories(filePath.getParent());

            // Создаем файл, если его не существует
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                System.out.println("The file successful create: " + filePath.toAbsolutePath());
            } else {
                System.out.println("The file already exists: "  + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating the file: " + e.getMessage());
        }

    }

    public void log(LevelLog logLevel, String message) {
        if (this.logLevel <= map.get(logLevel)) {
            LocalDateTime currentDateTime = LocalDateTime.now();

            // Создаем объект DateTimeFormatter для форматирования даты
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

            // Форматируем текущую дату и время в нужный формат и выводим на экран
            String formattedDateTime = currentDateTime.format(formatter);
            message = formattedDateTime + " " + message;
            try {
                Path relativePath = Paths.get(logPath, "connector.log");
                Files.writeString(relativePath, message + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
