package client;


import client.connector.Connector;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public PackageProcessor(Config config) {
        packageList = PackageConnector.connectPackages(config);
//        executor = new ScheduledThreadPoolExecutor(packageList.size());
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
            executor.shutdownNow();
        }));
    }

    public void start() {
        timerAuthorization(systemConfigA, timerAuthorizationA, authorizationA);
        timerAuthorization(systemConfigB, timerAuthorizationB, authorizationB);
        Integer count = 0;
        //ожидайка подключения
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (authorizationA.isAuthenticated() && authorizationB.isAuthenticated()) break;
        }
        for (LinkedPackage linkedPackage : packageList) {
            executor.scheduleAtFixedRate(() -> {
                try {
                    work(linkedPackage);
                } catch (ReceivingException e) {
                    System.err.println("Сломан у "+ linkedPackage.getAPackage().getId() + " приём пакетов " + e.getMessage());
                } catch (DispatchGETException e) {
                    System.err.println("Сломан у "+ linkedPackage.getAPackage().getId() + " отправка GET пакетов" + e.getMessage());
                } catch (DispatchPOSTException e) {
                    System.err.println("Сломан у "+ linkedPackage.getAPackage().getId() + " отправка POST пакетов" + e.getMessage());
                }
            }, 0, systemConfigA.getPackagesDelay(), TimeUnit.MILLISECONDS);
        }

    }

    private Package packageCollector(@NotNull Package packageTMP, SystemType systemType) {
        for (Parameter parameter : packageTMP.getRequestParams()) {
            if (parameter.getValue().equals("AUTH_TOKEN")) {
                if (systemType == SystemType.SYSTEM_TYPE_A) {
                    parameter.setValue(authorizationA.getToken());
                }
                if (systemType == SystemType.SYSTEM_TYPE_B) {
                    parameter.setValue(authorizationB.getToken());
                }
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

    @SneakyThrows
    private Package packageAssembly(@NotNull Package packageA, @NotNull Package packageB, @NotNull JSONObject response) {
        // сюда надо поставить рекурсию.

        ObjectMapper mapper = new ObjectMapper();
        Parameter requestParams = mapper.readValue(response.toString(), Parameter.class);
        System.out.println(response.toString());

//        for (Parameter parameterA : packageA.getResponseParams()) {
//            if (parameterA.getFlag()) {
//                for (Parameter parameterB : packageB.getRequestBody()) {
//                    if (parameterB.getName().equals(parameterA.getNameB())) {
//                        Parameter parameterTMP = parameterB;
//                        parameterTMP.setValue(response.getString(parameterA.getName()));
//                        packageB.getRequestBody().set(packageB.getRequestBody().indexOf(parameterB), parameterTMP);
//                        break;
//                    }
//                }
//            }
//        }
        return packageB;
    }

    public void work(@NotNull LinkedPackage linkedPackage) throws ReceivingException, DispatchGETException, DispatchPOSTException {
        Package packageA = linkedPackage.getAPackage();
        Package packageB = linkedPackage.getBPackage();
        String request = "";
        if (packageA.getMethod().equals(Method.GET)){
            String pack = systemConfigA.getDomain() + RequestProcessor.createUrl(packageCollector(packageA, SystemType.SYSTEM_TYPE_A));
            request = connectorA.sendGetRequest(pack);
        }
        if (packageA.getMethod().equals(Method.POST)){
            JSONObject pack = RequestProcessor.createJson(packageA.getRequestBody());
            request = connectorA.sendPostRequest(systemConfigA.getDomain() +packageA.getUrl(), pack);
        }
        System.err.println(request);
        JSONObject response = null;
        try {
            response = new JSONObject(request);
        } catch (JSONException e) {
            System.err.println("Ответ получил не json");
        }
//        Package packageAnswer = packageAssembly(packageA, packageB, response);
//        System.out.println(packageAnswer.getId());
//        JSONObject pack = RequestProcessor.createJson(packageB.getRequestBody());
        JSONObject pack = new  JSONObject();
        pack.put("sting", "string");
        request = connectorB.sendPostRequest(systemConfigB.getDomain() + packageB.getUrl(), pack);
        System.out.println(request);

    }

    private void timerAuthorization(@NotNull SystemConfig systemConfig, ScheduledExecutorService timerAuthorization, AuthorizationManager authorizationManager) {
        if (systemConfig.getAuthorization().getTimeoutUpdate() > 0)
            timerAuthorization.scheduleAtFixedRate(() -> {
            try {
                authorizationManager.authorize();
            } catch (AuthorizationTimeoutException e) {
                stop(e);
            }
            }, 0, systemConfig.getAuthorization().getTimeoutUpdate(), TimeUnit.SECONDS);
        else {
            try {
                authorizationManager.authorize();
            } catch (AuthorizationTimeoutException e) {
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
