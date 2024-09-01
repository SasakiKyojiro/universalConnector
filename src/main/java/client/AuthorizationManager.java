package client;

import client.connector.Connector;
import config.json.Authorization;
import config.json.Parameter;
import exception.AuthorizationTimeoutException;
import exception.DispatchPostException;
import exception.ReceivingException;
import log.LevelLog;
import log.LogUtil;
import lombok.Getter;
import org.json.JSONObject;


public class AuthorizationManager {
    @Getter
    private String token;
    @Getter
    private boolean authenticated = false;

    private final String domain;
    private String login;
    private String password;
    private final Connector connector;
    private final Authorization authorization;
    private final LogUtil logUtil;

    public AuthorizationManager(String domain, Connector connector, Authorization authorization, LogUtil logUtil) {
        this.domain = domain;
        this.connector = connector;
        this.authorization = authorization;
        this.logUtil = logUtil;
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
                    token = getAuthorizationToken();
                    authenticated = true;
                }
            }
            case PERMANENT_TOKEN -> {
                token = authorization.getParams().get(0).getValue();
                authenticated = true;
            }
            case NONE -> authenticated = true;
            default -> throw new IllegalStateException("Unexpected value: " + authorization.getType());
        }

    }

    private String getAuthorizationToken() throws AuthorizationTimeoutException {
        // Создаем JSON объект с логином и паролем
        JSONObject json = new JSONObject();
        json.put("login", login);
        json.put("password", password);
        // Обработка ответа и извлечение токена
        String jsonResponse = null;
        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                jsonResponse = connector.sendPostRequest(domain + authorization.getUrl(), json);
                break;
            } catch (DispatchPostException | ReceivingException e) {
                retryCount++;
                try {
                    logUtil.log(LevelLog.Error, "No connection to " + domain + authorization.getUrl());
                    System.err.println("No connection to " + domain + authorization.getUrl());
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if (retryCount == maxRetries) {
            throw new AuthorizationTimeoutException("Connection attempt limit exceeded " + domain);
        }
        return jsonResponse;
    }
}
