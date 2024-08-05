package client.connector;


import client.AuthorizationManager;
import config.json.Config;
import config.packages.LinkedPackage;
import config.packages.PackageConnector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackageProcessor {
    private Config config;
    private List<LinkedPackage> packageList;
    private ExecutorService executor;
    private AuthorizationManager authorizationA;
    private AuthorizationManager authorizationB;

    public PackageProcessor(Config config)  {
        this.config = config;
        this.packageList = PackageConnector.connectPackages(config);
        this.executor = Executors.newFixedThreadPool(packageList.size());
        authorizationA  = new AuthorizationManager(config.getSystemTypeA().getDomain());
        authorizationA.authorize(config.getSystemTypeA().getAuthorization());
        authorizationB = new AuthorizationManager(config.getSystemTypeB().getDomain());
        authorizationB.authorize(config.getSystemTypeB().getAuthorization());
    }

    public String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

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

    public String sendPostRequest(String urlString, String requestBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
            wr.write(postData);
        }

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

}
