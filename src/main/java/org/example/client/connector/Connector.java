package org.example.client.connector;

import org.example.config.types.SystemType;
import org.example.exception.DispatchGetException;
import org.example.exception.DispatchPostException;
import org.example.exception.DispatchPutException;
import org.example.exception.ReceivingException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class Connector {
    private final Integer timeOut;
    private final SystemType systemType;
    private TrustManager[] trustAllCerts;

    public Connector(Integer timeOut, SystemType systemType, Boolean useCertificate) {
        this.timeOut = timeOut;
        this.systemType = systemType;
        if (useCertificate) {
            trustAllCerts = new TrustManager[]{new CustomTrustManager()};
            SSLContext sc = null;
            try {
                sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
    }

    public String sendGetRequest(String urlString) throws DispatchGetException, ReceivingException {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException e) {
            throw new DispatchGetException(e.getMessage(), systemType);
        }
        connection.setConnectTimeout(timeOut);
        return processingResponse(connection);
    }

    public String sendPostRequest(String urlString, JSONObject requestBody) throws DispatchPostException, ReceivingException {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            DataOutputStream wr;

            wr = new DataOutputStream(connection.getOutputStream());
            byte[] postData = requestBody.toString().getBytes();
            wr.write(postData);
        } catch (IOException e) {
            throw new DispatchPostException(e.getMessage(), systemType);
        }
        return processingResponse(connection);
    }

    public String sendPutRequest(String urlString, JSONObject requestBody) throws DispatchPutException, ReceivingException {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setRequestMethod("PUT");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            DataOutputStream wr;

            wr = new DataOutputStream(connection.getOutputStream());
            byte[] postData = requestBody.toString().getBytes();
            wr.write(postData);
        } catch (IOException e) {
            throw new DispatchPutException(e.getMessage(), systemType);
        }
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

}
