package client;

import client.connector.Connector;
import config.json.Authorization;
import config.json.Parameter;
import lombok.Getter;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuthorizationManager {
    @Getter
    private String token;
    @Getter
    private Boolean authenticated = false;

    private String domain;
    private Timer timer = new Timer("AuthorizationManager", true);
    private String login;
    private String password;
    private Connector connector;
    CountDownLatch latch = new CountDownLatch(1);

    public AuthorizationManager(String domain, Connector connector) {
        this.domain = domain;
        this.connector = connector;
    }

    public void authorize(Authorization authorization, Boolean cikle) {
        System.out.println("Authorizing " + authorization.getName());
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
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                token = getToken();
                                authenticated = true;
                            } catch (Exception e) {
                                authenticated = false;
                                System.err.println("Превышен лимит попыток подключения");
                                timer.cancel();
                            }
                        }
                    };
                    timer.scheduleAtFixedRate(task, 0, authorization.getTimeoutUpdate());

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
        cikle = true;
    }

    @SneakyThrows
    private String getToken() {
        // Создаем JSON объект с логином и паролем
        JSONObject json = new JSONObject();
        json.put("login", login);
        json.put("password", password);
        // Обработка ответа и извлечение токена
        JSONObject jsonResponse = null;

        int maxRetries = 5;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                jsonResponse = connector.sendPostRequest(domain, json);
                break;
            } catch (IOException e) {
                retryCount++;
                System.err.println("Error: " + "Время ожидания превышено " + domain);
                Thread.sleep(1000);
            }
        }
        String token = jsonResponse.getString("token");
        return token;
    }
}
