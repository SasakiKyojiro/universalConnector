package log;

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
