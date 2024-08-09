package client.connector;

import exception.DispatchGETException;
import exception.DispatchPOSTException;
import exception.ReceivingException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Connector {
    private final Integer timeOut;

    public Connector(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String sendGetRequest(String urlString) throws DispatchGETException, ReceivingException {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new DispatchGETException(e.getMessage());
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new DispatchGETException(e.getMessage());
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new DispatchGETException(e.getMessage());
        }
        connection.setConnectTimeout(timeOut);
        return processingResponse(connection);
    }

    private String processingResponse(HttpURLConnection connection) throws ReceivingException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            throw new ReceivingException(e.getMessage());
        }
        StringBuilder response = new StringBuilder();
        String line = "";
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new ReceivingException(e.getMessage());
            }
            response.append(line);
        }
        try {
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
        } catch (IOException e) {
            throw new DispatchPOSTException(e.getMessage());
        }
        byte[] postData = requestBody.toString().getBytes();
        try {
            wr.write(postData);
        } catch (IOException e) {
            throw new DispatchPOSTException(e.getMessage());
        }
        return processingResponse(connection);

    }

    private @NotNull HttpURLConnection getHttpURLConnection(String urlString) throws DispatchPOSTException {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new DispatchPOSTException(e.getMessage());
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new DispatchPOSTException(e.getMessage());
        }
        connection.setConnectTimeout(timeOut);
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new DispatchPOSTException(e.getMessage());
        }
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }
}
