package client.connector;

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
    private Integer timeOut;
    public Connector(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String sendGetRequest(String urlString) throws DispatchGETException, ReceivingException {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException e) {
            throw new DispatchGETException(e.getMessage());
        }
        connection.setConnectTimeout(timeOut);
        return processingResponse(connection);
    }

    private String processingResponse(HttpURLConnection connection) throws ReceivingException {
        BufferedReader reader = null;
        StringBuilder response;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new ReceivingException(e.getMessage());
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
            throw new DispatchPOSTException(e.getMessage());
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
            throw new DispatchPOSTException(e.getMessage());
        }
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }
}
