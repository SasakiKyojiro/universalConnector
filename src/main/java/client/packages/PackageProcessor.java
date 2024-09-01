package client.packages;

import client.AuthorizationManager;
import client.connector.Connector;
import client.connector.JsonFileManager;
import config.json.Config;
import config.json.Package;
import config.json.Parameter;
import config.json.SystemConfig;
import config.packages.LinkedPackage;
import config.packages.PackageConnector;
import config.packages.RequestProcessor;
import config.types.Method;
import config.types.SystemType;
import exception.*;
import log.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static log.LevelLog.*;

public class PackageProcessor {
    private final List<LinkedPackage> packageList;
    private final AuthorizationManager authorizationA;
    private final AuthorizationManager authorizationB;
    private final Connector connectorA;
    private final Connector connectorB;
    private final SystemConfig systemConfigA;
    private final SystemConfig systemConfigB;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService timerAuthorizationA = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService timerAuthorizationB = Executors.newSingleThreadScheduledExecutor();
    private final LogUtil logUtil;
    private final Boolean buffering;
    private JsonFileManager jsonHandler;

    public PackageProcessor(Config config, LogUtil logUtil) {
        this.logUtil = logUtil;
        packageList = PackageConnector.connectPackages(config);
        systemConfigA = config.getSystemTypeA();
        systemConfigB = config.getSystemTypeB();
        connectorA = new Connector(systemConfigA.getTimeout(), SystemType.SYSTEM_TYPE_A);
        connectorB = new Connector(systemConfigB.getTimeout(), SystemType.SYSTEM_TYPE_B);
        authorizationA = new AuthorizationManager(systemConfigA.getDomain(), connectorA,
                systemConfigA.getAuthorization(), logUtil);
        authorizationB = new AuthorizationManager(systemConfigB.getDomain(), connectorB,
                systemConfigB.getAuthorization(), logUtil);

        if (config.isBuffering()) {
            buffering = true;
            jsonHandler = new JsonFileManager(config.getLogPath());
        } else
            buffering = false;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logUtil.log(Debug, "Shutting down");
            System.out.println("Shutting down...");
            if (buffering)
                jsonHandler.saveJsonToFile();
            timerAuthorizationA.shutdownNow();
            timerAuthorizationB.shutdownNow();
            executor.shutdownNow();
        }));
    }

    public void start() {
        timerAuthorization(systemConfigA, timerAuthorizationA, authorizationA);
        timerAuthorization(systemConfigB, timerAuthorizationB, authorizationB);
        // ожидание подключения
        do {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (!authorizationA.isAuthenticated() || !authorizationB.isAuthenticated());
        for (LinkedPackage linkedPackage : packageList)
            executor.scheduleAtFixedRate(() -> work(linkedPackage), 0, systemConfigA.getPackagesDelay(), TimeUnit.MILLISECONDS);
    }

    private Package packageCollector(@NotNull Package packageTMP) {
        for (Parameter parameter : packageTMP.getRequestParams()) {
            if (parameter.getValue().equals("AUTH_TOKEN")) {
                parameter.setValueTMP(authorizationA.getToken());
            }
            if (parameter.getValue().contains("NOW")) {
                String searchText = "|format|";
                int index = parameter.getValue().indexOf(searchText);
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern(parameter
                                .getValue()
                                .substring(index + searchText.length()));
                parameter.setValueTMP(currentDateTime.format(formatter));
            }
        }
        return packageTMP;
    }

    private void work(@NotNull LinkedPackage linkedPackage) {
        Package packageA = linkedPackage.getAPackage();
        Package packageB = linkedPackage.getBPackage();
        String request = "";
        try {
            if (packageA.getMethod().equals(Method.GET)) {
                String pack = systemConfigA.getDomain() + RequestProcessor.createUrl(packageCollector(packageA));
                request = connectorA.sendGetRequest(pack);
            }
            if (packageA.getMethod().equals(Method.POST)) {
                JSONObject pack = RequestProcessor.createJson(packageA.getRequestBody());
                request = connectorA.sendPostRequest(systemConfigA.getDomain() + packageA.getUrl(), pack);
            }
            JSONObject response = null;
            try {
                response = new JSONObject(request);
            } catch (JSONException e) {
                logUtil.log(Warning, "The response was received, but not in json format from the system A. Package ID: " + packageA.getId());
            }
            if (response != null) {
                JSONObject jsonObject = PackageRecursiveHandler.recursivePackage(packageA.getResponseParams(), response);
                jsonObject = PackageCollectorB.incompleteAssembler(jsonObject);
                String url = packageB.getUrl();
                if (!packageB.getRequestParams().isEmpty())
                    url = RequestProcessor.assemblyUrl(packageB, jsonObject);
                url = systemConfigB.getDomain() + url;
                try {
                    loadingFromBuffer(packageB);
                    if (packageB.getMethod().equals(Method.POST)) {
                        JSONObject pack = PackageCollectorB.assembly(packageB.getRequestBody(), jsonObject);
                        connectorB.sendPostRequest(url, pack);
                    }
                    if (packageB.getMethod().equals(Method.GET)) {
                        connectorB.sendGetRequest(url);
                    }
                    if (packageB.getMethod().equals(Method.PUT)) {
                        JSONObject pack = PackageCollectorB.assembly(packageB.getRequestBody(), jsonObject);
                        connectorB.sendPutRequest(url, pack);
                    }

                } catch (Exception e) {
                    logUtil.log(Error, "There is no connection to system B. Error: " + e.getMessage());
                    System.err.println("Package B " + packageB.getId() + " no connect " + e.getMessage());
                    if (buffering) jsonHandler.addJsonData(packageB.getId(), jsonObject);


                }
            }
        } catch (ReceivingException | DispatchGetException | DispatchPostException e) {
            if (e.getMessage().contains(String.valueOf(SystemType.SYSTEM_TYPE_A))) {
                logUtil.log(Fatal, e.getMessage());
                stop(e);
            }

        }

    }

    private void loadingFromBuffer(Package packageB) throws
            ReceivingException, DispatchPostException, DispatchGetException, DispatchPutException {
        if (buffering) {
            while (!jsonHandler.getJsonData().isEmpty()) {
                JSONObject jsonObject = jsonHandler.getFirstJsonObjectFromFile(packageB.getId());
                if (jsonObject != null) {
                    String url = packageB.getUrl();
                    if (!packageB.getRequestParams().isEmpty()){
                        url = RequestProcessor.assemblyUrl(packageB, jsonObject);
                    }
                    url = systemConfigB.getDomain() + url;
                    if (packageB.getMethod().equals(Method.POST)) {
                        connectorB.sendPostRequest(url, PackageCollectorB.assembly(packageB.getRequestBody(), jsonObject));
                        jsonHandler.deleteFirstJsonObjectFromFile(packageB.getId());
                    }
                    if (packageB.getMethod().equals(Method.GET)) {
                        connectorB.sendGetRequest(url);
                        jsonHandler.deleteFirstJsonObjectFromFile(packageB.getId());
                    }
                    if (packageB.getMethod().equals(Method.PUT)) {
                        connectorB.sendPutRequest(url, PackageCollectorB.assembly(packageB.getRequestBody(), jsonObject));
                        jsonHandler.deleteFirstJsonObjectFromFile(packageB.getId());
                    }
                } else break;
            }
        }
    }

    private void timerAuthorization(@NotNull SystemConfig systemConfig, ScheduledExecutorService timerAuthorization, AuthorizationManager authorizationManager) {
        if (systemConfig.getAuthorization().getTimeoutUpdate() > 0)
            timerAuthorization.scheduleAtFixedRate(() -> {
            try {
                authorizationManager.authorize();
            } catch (AuthorizationTimeoutException e) {
                logUtil.log(Fatal, "The waiting time for authorization has been exceeded " + systemConfig.getDomain());
                stop(e);
            }
            }, 0, systemConfig.getAuthorization().getTimeoutUpdate(), TimeUnit.SECONDS);
        else {
            try {
                authorizationManager.authorize();
            } catch (AuthorizationTimeoutException e) {
                logUtil.log(Fatal, "Connection/authorization error to " + systemConfig.getDomain());
                stop(e);
            }
        }
    }

    private void stop(@NotNull Exception e) {
        System.err.println(e.getMessage());
        timerAuthorizationA.shutdown();
        timerAuthorizationB.shutdown();
        executor.shutdown();
        System.exit(444);
    }
}
