package client;

import config.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class RestClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Configuration.SystemConfig config;

    public RestClient(Configuration.SystemConfig config) {
        this.config = config;
    }

    private HttpRequest.Builder createRequestBuilder(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.domain + endpoint))
                .timeout(java.time.Duration.ofMillis(config.timeout));

        if (config.use_auth) {
            if ("AUTH-TOKEN".equalsIgnoreCase(config.authorization.type)) {
                String token = getToken();
                builder.header("Authorization", "Bearer " + token);
            } else if ("PERMANENT-TOKEN".equalsIgnoreCase(config.authorization.type)) {
                String token = config.authorization.params.stream()
                        .filter(param -> "token".equals(param.name))
                        .map(param -> param.value)
                        .findFirst()
                        .orElse("");
                builder.header("Authorization", "Bearer " + token);
            }
        }

        return builder;
    }

    private String getToken() {
        // Implement token retrieval logic
        return "your_token";
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
