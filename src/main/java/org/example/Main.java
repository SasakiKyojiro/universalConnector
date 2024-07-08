package org.example;

import config.Configuration;
import connector.Connector;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "config.json";  // Path to the config file in resources
        try {
            Configuration config = Configuration.load(configFilePath);
            Connector connector = new Connector(config);
            // Define endpoints for System A and System B
            String endpointA = "/data";
            String endpointB = "/receive";
            connector.transferData(endpointA, endpointB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
