package org.example;

import client.packages.PackageProcessor;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import config.inspector.AvailabilityInspector;
import config.inspector.QualityInspector;
import config.json.Config;
import config.parser.ConfigParser;
import log.LevelLog;
import log.LogUtil;
import utils.AppCommander;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static log.LevelLog.*;

public class Main {
    private static final AppCommander commander = new AppCommander();
    private static final JCommander jCommander = JCommander.newBuilder().addObject(commander).build();
    private static Config config = null;
    private static boolean parsedItCorrectly = false;

    public static void main(String[] args) {
        try {
            jCommander.parse(args);
        }
        catch (ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            jCommander.usage();
        }
        if(!commander.pathToConfig.isEmpty())
        {
            String configFilePath = commander.pathToConfig;
            try {
                System.out.println("Начат парсинг");
                ConfigParser configParser = new ConfigParser();
                config = configParser.parseConfig(configFilePath);
                if (config != null) {
                    parsedItCorrectly = AvailabilityInspector.availabilityInspector(config);
                    if(parsedItCorrectly){
                        parsedItCorrectly = QualityInspector.qualityInspector(config);
                        if(!parsedItCorrectly) System.err.println("Error filling in one of the packages in \"packages\"");
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        else
            System.err.println("No config file specified.");
        if (config != null && parsedItCorrectly) {
            config.setLogPath(System.getProperty("user.dir")+config.getLogPath());
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
