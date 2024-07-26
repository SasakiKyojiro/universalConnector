package log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LogUtil {
    public static void log(String logPath, String message) {
        try {
            Path path = Paths.get(logPath, "connector.log");
            // Ensure the directory exists
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, message + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
