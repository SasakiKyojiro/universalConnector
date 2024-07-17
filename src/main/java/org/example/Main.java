package org.example;

import config.Config;
import config.ConfigParser;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "config.json";  // Путь к файлу конфигурации
        Config config = null;

        ConfigParser configParser = new ConfigParser();
        config = configParser.parseConfig(configFilePath);
//        try {
//            ConfigParser configParser = new ConfigParser();
//            config = configParser.parseConfig(configFilePath);
//
//            if (config != null && config.isLogging()) {
//                LogUtil.log(config.getLogPath(), "Starting connector");
//            }
//
//            Connector connector = new Connector(config);
//            connector.transferData();
//        } catch (IOException e) {
//            e.printStackTrace();
//            if (config != null && config.isLogging()) {
//                LogUtil.log(config.getLogPath(), "Failed to load configuration: " + e.getMessage());
//            }
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
    }
}
