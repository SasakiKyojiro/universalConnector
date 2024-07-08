package connector;

import client.RestClient;
import config.Configuration;
import log.LogUtil;

import java.net.http.HttpResponse;

public class Connector {
    private final Configuration config;
    private final RestClient clientA;
    private final RestClient clientB;

    public Connector(Configuration config) {
        this.config = config;
        this.clientA = new RestClient(config.system_type_a);
        this.clientB = new RestClient(config.system_type_b);
    }

    public void transferData(String endpointA, String endpointB) {
        try {
            HttpResponse<String> responseA = clientA.get(endpointA);
            if (responseA.statusCode() == 200) {
                String data = responseA.body();
                retrySend(data, endpointB);
                if (config.logging.enabled) {
                    // Log successful transfer
                    LogUtil.log(config.logging.log_path, "Transferred data: " + data);
                }
            } else {
                // Handle error from system A
                if (config.logging.enabled) {
                    LogUtil.log(config.logging.log_path, "Failed to fetch data from System A: " + responseA.body());
                }
            }
        } catch (Exception e) {
            if (config.logging.enabled) {
                LogUtil.log(config.logging.log_path, "Error during data transfer: " + e.getMessage());
            }
        }
    }

    private void retrySend(String data, String endpointB) {
        int attempts = 0;
        boolean success = false;
        while (!success && attempts < config.system_type_b.authorization.timeout_update) {
            try {
                HttpResponse<String> responseB = clientB.post(endpointB, data);
                if (responseB.statusCode() == 200 || responseB.statusCode() == 201) {
                    success = true;
                } else {
                    attempts++;
                    Thread.sleep(config.system_type_b.authorization.timeout_update);
                }
            } catch (Exception e) {
                attempts++;
                try {
                    Thread.sleep(config.system_type_b.authorization.timeout_update);
                } catch (InterruptedException ie) {
                    // Handle interrupted exception
                }
            }
        }

        if (!success && config.logging.enabled) {
            LogUtil.log(config.logging.log_path, "Failed to send data to System B after " + attempts + " attempts");
        }
    }
}
