package client;

import config.json.Authorization;
import config.json.Parameter;
import lombok.Getter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuthorizationManager {
    @Getter
    private String token;

    private final String domain;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private String login;
    private String password;

    public AuthorizationManager(String domain) {
        this.domain = domain;
    }

    public void authorize(Authorization authorization) {
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
                    scheduler.scheduleWithFixedDelay(() -> {
                        try {
                            token = getToken();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, 0, authorization.getTimeoutUpdate(), TimeUnit.SECONDS);
                }
            }
            case PERMANENT_TOKEN -> {
                token = authorization.getParams().get(0).getValue();
            }
            case NONE -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + authorization.getType());
        }

    }

    private String getToken() throws Exception {
        URL url = new URL(domain);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Создаем JSON объект с логином и паролем
        JSONObject json = new JSONObject();
        json.put("login", login);
        json.put("password", password);

        // Устанавливаем JSON как тело запроса
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            byte[] postData = json.toString().getBytes();
            wr.write(postData);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        // Обработка ответа и извлечение токена
        JSONObject jsonResponse = new JSONObject(response.toString());
        String token = jsonResponse.getString("token");

        return token;
    }
}
