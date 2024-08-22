package org.example;

import client.packages.PackageProcessor;
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
        String configFilePath = "config.json";
        Config config = null;
        boolean parsedItCorrectly = false;
        try {
            System.out.println("Начат парсинг");
            ConfigParser configParser = new ConfigParser();
            config = configParser.parseConfig(configFilePath);
            if (config != null) {
                parsedItCorrectly = AvailabilityInspector.availabilityInspector(config);
                if(parsedItCorrectly){
                    parsedItCorrectly = QualityInspector.qualityInspector(config);
                    if(!parsedItCorrectly){
                        System.err.println("Error filling in one of the packages in \"packages\"");
                    }
                }
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
            logUtil.log(Debug, "Launching service connections.");
            PackageProcessor processor = new PackageProcessor(config, logUtil);
            processor.start();
        }
    }
}
