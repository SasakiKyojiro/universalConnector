package config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Configuration {
    public Logging logging;
    public Map<String, SystemConfig> systems;

    public static class Logging {
        public boolean enabled;
        public String log_path;
    }

    public static class SystemConfig {
        public int timeout;
        public String domain;
        public boolean use_auth;
        public AuthorizationConfig authorization;
        public int packages_delay;
        public Package[] packages;

        public static class AuthorizationConfig {
            public String type;
            public String login_url;
            public String method;
            public Map<String, String> params;
            public Map<String, String> headers;
            public String response_token_field;
            public boolean need_update;
            public int update_interval;
        }

        public static class Package {
            public int priority;
            public String key;
            public String method;
            public String url;
            public int delay;
            public Map<String, String> path_variables;
            public Map<String, String> request_params;
            public Map<String, Object> request_body;
            public Map<String, Object> response_params;
        }
    }

    public static Configuration load(String resourcePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return mapper.readValue(inputStream, Configuration.class);
        }
    }
}
