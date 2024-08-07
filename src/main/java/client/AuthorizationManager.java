package client;

import client.connector.Connector;
import config.json.Authorization;
import config.json.Parameter;
import exception.AuthorizationTimeoutException;
import lombok.Getter;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AuthorizationManager {
    @Getter
    private String token;
    @Getter
    private Boolean authenticated = false;

    private String domain;
    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    private String login;
    private String password;
    private Connector connector;
    private Authorization authorization;

    public AuthorizationManager(String domain, Connector connector, Authorization authorization) {
        this.domain = domain;
        this.connector = connector;
        this.authorization = authorization;
    }

    public void authorize() throws AuthorizationTimeoutException {
        System.out.println("Authorizing " + authorization.getName() + " " + domain);
        switch (authorization.getType()) {
            case AUTH_TOKEN -> {
                for (Parameter parameter : authorization.getParams()) {
                    if (parameter.getName().equals("login")) {
                        login = parameter.getValue();
                    }
                    if (parameter.getName().equals("password")) {
                        password = parameter.getValue();
                    }
                }
                if (authorization.isNeedLogging()) {
                    token = getToken();
                }
            }
            case PERMANENT_TOKEN -> {
                token = authorization.getParams().get(0).getValue();
                authenticated = true;
            }
            case NONE -> {
                authenticated = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + authorization.getType());
        }

    }

    private String getToken() throws AuthorizationTimeoutException {
        // Создаем JSON объект с логином и паролем
        JSONObject json = new JSONObject();
        json.put("login", login);
        json.put("password", password);
        // Обработка ответа и извлечение токена
        JSONObject jsonResponse = null;

        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                jsonResponse = connector.sendPostRequest(domain, json);
                break;
            } catch (IOException e) {
                retryCount++;
                System.err.println("Error: " + "Время ожидания превышено " + domain);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if (retryCount == maxRetries) {
            throw new AuthorizationTimeoutException("Превышен лимит попыток подключения");
        }
        String token = jsonResponse.getString("token");
        return token;
    }
}
