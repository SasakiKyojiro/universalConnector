package client;


import client.connector.Connector;
import config.json.Config;
import config.json.SystemConfig;
import config.packages.LinkedPackage;
import config.packages.PackageConnector;
import config.packages.RequestProcessor;
import config.types.Method;
import config.types.ParameterType;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackageProcessor {
    private Config config;
    private List<LinkedPackage> packageList;
    private ExecutorService executor;
    private AuthorizationManager authorizationA;
    private AuthorizationManager authorizationB;
    private Connector connectorA;
    private Connector connectorB;

    public PackageProcessor(Config config)  {
        this.config = config;
        this.packageList = PackageConnector.connectPackages(config);
        this.executor = Executors.newFixedThreadPool(packageList.size());
        SystemConfig systemConfigA = config.getSystemTypeA();
        SystemConfig systemConfigB = config.getSystemTypeB();
        connectorA = new Connector(systemConfigA.getTimeout());
        connectorB = new Connector(systemConfigB.getTimeout());
        System.out.println("Подключение к "+ systemConfigA.getDomain());
        authorizationA = new AuthorizationManager(systemConfigA.getDomain(), connectorA);
        System.out.println("Подключение к "+ systemConfigB.getDomain());
        authorizationB = new AuthorizationManager(systemConfigB.getDomain(), connectorB);
        authorizationA.authorize(systemConfigA.getAuthorization());
        authorizationB.authorize(systemConfigB.getAuthorization());

        System.out.println(authorizationA.getAuthenticated());
        System.out.println(authorizationB.getAuthenticated());
    }
    public void start() {
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
