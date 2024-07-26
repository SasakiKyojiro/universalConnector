package org.example;

import config.Config;
import config.ConfigParser;
import config.packages.RequestProcessor;
import log.LogUtil;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "config.json";  // Путь к файлу конфигурации
        Config config = null;
//        LogUtil logger = new LogUtil();
        try {
            System.out.println("Начат парсинг");
            ConfigParser configParser = new ConfigParser();
            config = configParser.parseConfig(configFilePath);
            if (config != null && config.isLogging()) {
                System.out.println("Успешно завершён парсинг");
//                logger.log(config.getLog_path(), "Starting connector");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            if (config != null && config.isLogging()) {
//               logger.log(config.getLog_path(), "Failed to load configuration: " + e.getMessage());
//            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        RequestProcessor requestProcessor = new RequestProcessor();
        List<String> requests = requestProcessor.processConfig(config);
        System.out.println("Пакеты собраны " + requests);
    }
}
