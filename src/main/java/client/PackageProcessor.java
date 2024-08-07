package client;


import client.connector.Connector;
import config.json.Config;
import config.json.SystemConfig;
import config.packages.LinkedPackage;
import config.packages.PackageConnector;
import config.packages.RequestProcessor;
import config.types.Method;
import exception.AuthorizationTimeoutException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class PackageProcessor {
    private Config config;
    private List<LinkedPackage> packageList;
    private ExecutorService executor;
    private AuthorizationManager authorizationA;
    private AuthorizationManager authorizationB;
    private Connector connectorA;
    private Connector connectorB;
    private SystemConfig systemConfigA;
    private SystemConfig systemConfigB;

    private ScheduledExecutorService timerAuthorizationA = new ScheduledThreadPoolExecutor(1);
    private ScheduledExecutorService timerAuthorizationB = new ScheduledThreadPoolExecutor(1);

    public PackageProcessor(Config config) {
        this.config = config;
        packageList = PackageConnector.connectPackages(config);
        executor = Executors.newScheduledThreadPool(packageList.size());
        systemConfigA = config.getSystemTypeA();
        systemConfigB = config.getSystemTypeB();
        connectorA = new Connector(systemConfigA.getTimeout());
        connectorB = new Connector(systemConfigB.getTimeout());
        authorizationA = new AuthorizationManager(systemConfigA.getDomain(), connectorA, systemConfigA.getAuthorization());
        authorizationB = new AuthorizationManager(systemConfigB.getDomain(), connectorB, systemConfigB.getAuthorization());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            timerAuthorizationA.shutdownNow();
            timerAuthorizationB.shutdownNow();
        }));
    }
    public void start() {
        timerAuthorizationA.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Authorization A");
                authorizationA.authorize();
            } catch (Exception e ) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }, 0, systemConfigA.getTimeout(), TimeUnit.SECONDS);

        timerAuthorizationB.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Authorization B");
                authorizationB.authorize();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(2);
            }
        }, 0, systemConfigA.getTimeout(), TimeUnit.SECONDS);

        for (LinkedPackage linkedPackage : packageList) {
            executor.submit(()->{
               if (linkedPackage.getAPackage().getMethod().equals(Method.GET)){
                   String pack = RequestProcessor.createUrl(linkedPackage.getAPackage());
                   try {
                       String request = connectorA.sendGetRequest(pack);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               }
            });
        }
    }
}
