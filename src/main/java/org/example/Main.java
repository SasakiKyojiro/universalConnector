package org.example;

import client.PackageProcessor;
import config.inspector.AvailabilityInspector;
import config.inspector.QualityInspector;
import config.json.Config;
import config.parser.ConfigParser;
import log.LevelLog;
import log.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static log.LevelLog.*;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "config.json";  // Путь к файлу конфигурации
        Config config = null;
        boolean parsedItCorrectly = false;
//        LogUtil logger = new LogUtil();
        try {
            System.out.println("Начат парсинг");
            ConfigParser configParser = new ConfigParser();
            config = configParser.parseConfig(configFilePath);
            if (config != null) {
                parsedItCorrectly = AvailabilityInspector.availabilityInspector(config);
                if(parsedItCorrectly){
                    parsedItCorrectly = QualityInspector.qualityInspector(config);
                    if(!parsedItCorrectly){
                        System.err.println("Ошибка в заполнении одного из пакетов в \"packages\"");
                    }
                }
//                logger.log(config.getLog_path(), "Starting connector");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (config != null && parsedItCorrectly) {
            Map<String, LevelLog> logLevelMap = new HashMap<>();
            logLevelMap.put(Debug.toString(), Debug);
            logLevelMap.put(Error.toString(), Error);
            logLevelMap.put(Fatal.toString(), Fatal);
            logLevelMap.put(Warning.toString(), Warning);

            LevelLog level = logLevelMap.get(config.getLogLevel());
            LogUtil logUtil = new LogUtil(config.getLogPath(), level);
            logUtil.log(Debug, "Запуск подключений к сервисам.");
            PackageProcessor processor = new PackageProcessor(config, logUtil);
            processor.start();
        }
    }
}
