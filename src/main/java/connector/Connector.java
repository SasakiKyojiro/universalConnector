package connector;


import client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.Config;
import config.System;
import log.LogUtil;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Connector {
//    private final Config config;
//    private final RestClient clientA;
//    private final RestClient clientB;
//
//    public Connector(Config config) throws Exception {
//        this.config = config;
//        this.clientA = new RestClient(config.getSystemTypeA());
//        this.clientB = new RestClient(config.getSystemTypeB());
//    }
//
//    public void transferData() {
//        System systemA = config.getSystemTypeA();
//        Configuration.SystemConfig.Package[] packages = systemA.packages;
//        Arrays.sort(packages, (p1, p2) -> Integer.compare(p1.priority, p2.priority));
//
//        for (Configuration.SystemConfig.Package pkg : packages) {
//            try {
//                HttpResponse<String> response = null;
//
//                switch (pkg.method.toUpperCase()) {
//                    case "GET":
//                        response = clientA.get(pkg.url);
//                        break;
//                    case "POST":
//                        response = clientA.post(pkg.url, buildRequestBody(pkg.request_body));
//                        break;
//                    case "PUT":
//                        response = clientA.put(pkg.url, buildRequestBody(pkg.request_body));
//                        break;
//                }
//
//                if (response != null && response.statusCode() == 200) {
//                    String data = response.body();
//                    retrySend(data, pkg);
//                    if (config.logging.enabled) {
//                        LogUtil.log(config.logging.log_path, "Transferred data: " + data);
//                    }
//                } else {
//                    if (config.logging.enabled) {
//                        LogUtil.log(config.logging.log_path, "Failed to fetch data from System A: " + (response != null ? response.body() : "null response"));
//                    }
//                }
//
//                Thread.sleep(pkg.delay);
//            } catch (IOException | InterruptedException e) {
//                if (config.logging.enabled) {
//                    LogUtil.log(config.logging.log_path, "Error during data transfer: " + e.getMessage());
//                }
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private String buildRequestBody(Map<String, Object> params) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode body = mapper.createObjectNode();
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            appendToBody(body, entry.getKey(), entry.getValue());
//        }
//        return mapper.writeValueAsString(body);
//    }
//
//    private void appendToBody(ObjectNode body, String key, Object value) {
//        ObjectMapper mapper = new ObjectMapper();
//        if (value instanceof String) {
//            body.put(key, (String) value);
//        } else if (value instanceof Integer) {
//            body.put(key, (Integer) value);
//        } else if (value instanceof Boolean) {
//            body.put(key, (Boolean) value);
//        } else if (value instanceof Map) {
//            ObjectNode nestedObject = mapper.createObjectNode();
//            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
//                appendToBody(nestedObject, entry.getKey(), entry.getValue());
//            }
//            body.set(key, nestedObject);
//        } else if (value instanceof List) {
//            ArrayNode arrayNode = mapper.createArrayNode();
//            for (Object item : (List<Object>) value) {
//                if (item instanceof String) {
//                    arrayNode.add((String) item);
//                } else if (item instanceof Integer) {
//                    arrayNode.add((Integer) item);
//                } else if (item instanceof Boolean) {
//                    arrayNode.add((Boolean) item);
//                } else if (item instanceof Map) {
//                    ObjectNode nestedObject = mapper.createObjectNode();
//                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) item).entrySet()) {
//                        appendToBody(nestedObject, entry.getKey(), entry.getValue());
//                    }
//                    arrayNode.add(nestedObject);
//                }
//            }
//            body.set(key, arrayNode);
//        } else {
//            body.putPOJO(key, value);
//        }
//    }
//
//    private void retrySend(String data, Configuration.SystemConfig.Package pkg) {
//        int attempts = 0;
//        boolean success = false;
//        Configuration.SystemConfig systemB = config.systems.get("type_b");
//        while (!success && attempts < systemB.authorization.update_interval) {
//            try {
//                HttpResponse<String> response = clientB.post(pkg.url, data);
//                if (response.statusCode() == 200 || response.statusCode() == 201) {
//                    success = true;
//                } else {
//                    attempts++;
//                    Thread.sleep(systemB.authorization.update_interval);
//                }
//            } catch (Exception e) {
//                attempts++;
//                try {
//                    Thread.sleep(systemB.authorization.update_interval);
//                } catch (InterruptedException ie) {
//                    // Handle interrupted exception
//                }
//            }
//        }
//
//        if (!success && config.logging.enabled) {
//            LogUtil.log(config.logging.log_path, "Failed to send data to System B after " + attempts + " attempts");
//        }
//    }
}
