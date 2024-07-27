package log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LogUtil {
    public static void log(String logPath, String message) {
        try {
            String currentDir = System.getProperty("user.dir");
            Path relativePath = Paths.get(currentDir, logPath, "connector.log");
            // Ensure the directory exists
            if (Files.notExists(relativePath.getParent())) {
                Files.createDirectories(relativePath.getParent());
            }
            Files.writeString(relativePath, message + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
