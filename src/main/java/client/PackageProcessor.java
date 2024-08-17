package client;

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
import exception.AuthorizationTimeoutException;
import exception.DispatchGETException;
import exception.DispatchPOSTException;
import exception.ReceivingException;
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
    private final boolean buffering;
    private JsonFileManager jsonHandler;

    public PackageProcessor(Config config, LogUtil logUtil) {
        this.logUtil = logUtil;
        packageList = PackageConnector.connectPackages(config);
        systemConfigA = config.getSystemTypeA();
        systemConfigB = config.getSystemTypeB();
        connectorA = new Connector(systemConfigA.getTimeout(), SystemType.SYSTEM_TYPE_A);
        connectorB = new Connector(systemConfigB.getTimeout(), SystemType.SYSTEM_TYPE_B);
        authorizationA = new AuthorizationManager(systemConfigA.getDomain(), connectorA, systemConfigA.getAuthorization());
        authorizationB = new AuthorizationManager(systemConfigB.getDomain(), connectorB, systemConfigB.getAuthorization());

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
        for (LinkedPackage linkedPackage : packageList) {
            executor.scheduleAtFixedRate(() -> {
                work(linkedPackage);
            }, 0, systemConfigA.getPackagesDelay(), TimeUnit.MILLISECONDS);
        }

    }

    private Package packageCollector(@NotNull Package packageTMP, SystemType systemType) {
        for (Parameter parameter : packageTMP.getRequestParams()) {
            if (parameter.getValue().equals("AUTH_TOKEN")) {
                if (systemType.equals(SystemType.SYSTEM_TYPE_A))
                    parameter.setValue(authorizationA.getToken());
                if (systemType.equals(SystemType.SYSTEM_TYPE_B))
                    parameter.setValue(authorizationB.getToken());
            }
            if (parameter.getValue().contains("NOW")) {
                String searchText = "|format|";
                int index = parameter.getValue().indexOf(searchText);
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern(parameter
                                .getValue()
                                .substring(index + searchText.length()));
                parameter.setValue(currentDateTime.format(formatter));
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
                String pack = systemConfigA.getDomain() + RequestProcessor.createUrl(packageCollector(packageA, SystemType.SYSTEM_TYPE_A));
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
                logUtil.log(Warning, "Ответ получен, но не формата json от системы А. ID пакета: " + packageA.getId());
            }
            if (response != null) {
                JSONObject jsonObject = PackageRecursiveHandler.recursivePackage(packageA.getResponseParams(), response);
                JSONObject pack = PackageCollectorB.assembly(packageB.getRequestBody(), jsonObject);
                try {
                    loadingFromBuffer(packageB);
                    connectorB.sendPostRequest(systemConfigB.getDomain() + packageB.getUrl(), pack);
                } catch (DispatchPOSTException e) {
                    logUtil.log(Error, "Нет подключения к системе Б");
                    if (buffering) jsonHandler.addJsonData(packageB.getId(), pack);
                }
            }
        } catch (ReceivingException | DispatchGETException | DispatchPOSTException e) {
            if (e.getMessage().contains(String.valueOf(SystemType.SYSTEM_TYPE_A))) {
                logUtil.log(Fatal, e.getMessage());
                stop(e);
            }
        }

    }

    private void loadingFromBuffer(Package packageB) throws ReceivingException, DispatchPOSTException {
        if (buffering) {
            while (!jsonHandler.getJsonData().isEmpty()) {
                JSONObject jsonObject = jsonHandler.getFirstJsonObjectFromFile(packageB.getId());
                if (jsonObject != null) {
                    connectorB.sendPostRequest(systemConfigB.getDomain() + packageB.getUrl(), jsonObject);
                    jsonHandler.deleteFirstJsonObjectFromFile(packageB.getId());
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
                logUtil.log(Fatal, "Превышено время ожидания авторизации у " + systemConfig.getDomain());
                stop(e);
            }
            }, 0, systemConfig.getAuthorization().getTimeoutUpdate(), TimeUnit.SECONDS);
        else {
            try {
                authorizationManager.authorize();
            } catch (AuthorizationTimeoutException e) {
                logUtil.log(Fatal, "Ошибка подключения/авторизации к " + systemConfig.getDomain());
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
