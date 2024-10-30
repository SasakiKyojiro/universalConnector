package org.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.example.client.packages.PackageProcessor;
import org.example.config.inspector.AvailabilityInspector;
import org.example.config.inspector.QualityInspector;
import org.example.config.json.Config;
import org.example.config.parser.ConfigParser;
import org.example.log.LevelLog;
import org.example.log.LogUtil;
import org.example.utils.AppCommander;

import java.util.HashMap;
import java.util.Map;

import static org.example.log.LevelLog.*;

public class Main {
    private static final AppCommander commander = new AppCommander();
    private static final JCommander jCommander = JCommander.newBuilder().addObject(commander).build();
    private static Config config = null;
    private static boolean parsedItCorrectly = false;

    public static void main(String[] args) {
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            jCommander.usage();
        }
        if (!commander.pathToConfig.isEmpty()) {
            String configFilePath = commander.pathToConfig;
            System.out.println("Начат парсинг");
            ConfigParser configParser = new ConfigParser();
            config = configParser.parseConfig(configFilePath);
            if (config != null) {
                parsedItCorrectly = AvailabilityInspector.availabilityInspector(config);
                if (parsedItCorrectly) {
                    parsedItCorrectly = QualityInspector.qualityInspector(config);
                    if (!parsedItCorrectly) System.err.println("Error filling in one of the packages in \"packages\"");
                }
            }
        } else System.err.println("No config file specified.");
        if (config != null && parsedItCorrectly) {
            config.setLogPath(System.getProperty("user.dir") + config.getLogPath());
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
