package client.connector;

import config.types.SystemType;
import exception.DispatchGETException;
import exception.DispatchPOSTException;
import exception.ReceivingException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connector {
    private final Integer timeOut;
    private final SystemType systemType;

    public Connector(Integer timeOut, SystemType systemType) {
        this.timeOut = timeOut;
        this.systemType = systemType;
    }

    public String sendGetRequest(String urlString) throws DispatchGETException, ReceivingException {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException e) {
            throw new DispatchGETException(e.getMessage(), systemType);
        }
        connection.setConnectTimeout(timeOut);
        return processingResponse(connection);
    }

    private String processingResponse(HttpURLConnection connection) throws ReceivingException {
        BufferedReader reader;
        StringBuilder response;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new ReceivingException(e.getMessage(), systemType);
        }
        connection.disconnect();
        return response.toString();
    }

    public String sendPostRequest(String urlString, JSONObject requestBody) throws DispatchPOSTException, ReceivingException {
        HttpURLConnection connection = getHttpURLConnection(urlString);
        DataOutputStream wr;
        try {
            wr = new DataOutputStream(connection.getOutputStream());
            byte[] postData = requestBody.toString().getBytes();
            wr.write(postData);
        } catch (IOException e) {
            throw new DispatchPOSTException(e.getMessage(), systemType);
        }
        return processingResponse(connection);

    }

    private @NotNull HttpURLConnection getHttpURLConnection(String urlString) throws DispatchPOSTException {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setRequestMethod("POST");
        } catch (IOException e) {
            throw new DispatchPOSTException(e.getMessage(), systemType);
        }
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }
}
