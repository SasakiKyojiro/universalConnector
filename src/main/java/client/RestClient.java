package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;


public class RestClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Configuration.SystemConfig config;
    private String authToken;

    public RestClient(Configuration.SystemConfig config) {
        this.config = config;
        if (config.use_auth && "CUSTOM".equalsIgnoreCase(config.authorization.type)) {
            authenticate();
        }
    }

    private void authenticate() {
        try {
            HttpRequest request = createAuthRequest();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.readValue(response.body(), ObjectNode.class);
                authToken = node.get(config.authorization.response_token_field).asText();
            } else {
                throw new RuntimeException("Failed to authenticate: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to authenticate", e);
        }
    }

    private HttpRequest createAuthRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.domain + config.authorization.login_url))
                .timeout(java.time.Duration.ofMillis(config.timeout));

        for (Map.Entry<String, String> header : config.authorization.headers.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        for (Map.Entry<String, String> param : config.authorization.params.entrySet()) {
            body.put(param.getKey(), param.getValue());
        }

        return builder.method(config.authorization.method, HttpRequest.BodyPublishers.ofString(body.toString())).build();
    }

    private HttpRequest.Builder createRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.domain + endpoint)).timeout(java.time.Duration.ofMillis(config.timeout));

        if (config.use_auth && authToken != null) {
            builder.header("Authorization", "Bearer " + authToken);
        }

        return builder;
    }

    public HttpResponse<String> get(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = createRequestBuilder(endpoint).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> post(String endpoint, String body) throws IOException, InterruptedException {
        HttpRequest request = createRequestBuilder(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> put(String endpoint, String body) throws IOException, InterruptedException {
        HttpRequest request = createRequestBuilder(endpoint)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
