package client.connector;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Connector {
    private final Integer timeOut;

    public Connector(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(timeOut);
        return processingResponse(connection);
    }

    private String processingResponse(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        return response.toString();
    }

    public String sendPostRequest(String urlString, JSONObject requestBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(timeOut);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        System.out.println(requestBody.toString());
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            byte[] postData = requestBody.toString().getBytes();
            wr.write(postData);
        }

        return processingResponse(connection);
    }
}
