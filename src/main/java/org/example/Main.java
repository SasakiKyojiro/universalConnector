package org.example;

import config.Configuration;
import connector.Connector;
import log.LogUtil;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "config.json";  // Path to the config file in resources
        Configuration config = null;

        try {
            config = Configuration.load(configFilePath);
            if (config.logging.enabled) {
                LogUtil.log(config.logging.log_path, "Starting connector");
            }
            Connector connector = new Connector(config);
            connector.transferData();
        } catch (IOException e) {
            e.printStackTrace();
            if (config != null && config.logging.enabled) {
                LogUtil.log(config.logging.log_path, "Failed to load configuration: " + e.getMessage());
            }
        }
    }
}
