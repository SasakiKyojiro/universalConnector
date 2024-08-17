package org.example;

import client.PackageProcessor;
import config.json.Config;
import config.parser.ConfigParser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "config.json";  // Путь к файлу конфигурации
        Config config = null;
//        LogUtil logger = new LogUtil();
        try {
            System.out.println("Начат парсинг");
            ConfigParser configParser = new ConfigParser();
            config = configParser.parseConfig(configFilePath);
            if (config != null) {
                System.out.println("Успешно завершён парсинг");
//                logger.log(config.getLog_path(), "Starting connector");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (config != null) {
            PackageProcessor processor = new PackageProcessor(config);
            processor.start();

        }
    }
}
