package config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import java.util.List;

public class Configuration {
    public Logging logging;
    public SystemConfig system_type_a;
    public SystemConfig system_type_b;

    public static class Logging {
        public boolean enabled;
        public String log_path;
    }

    public static class SystemConfig {
        public int timeout;
        public String domain;
        public boolean use_auth;
        public Authorization authorization;
        public int packages_delay;
        public List<Package> packages;

        public static class Authorization {
            public String type;
            public String name;
            public boolean need_login;
            public String method;
            public List<Param> params;
            public boolean need_update;
            public int timeout_update;
        }

        public static class Package {
            public int priority;
            public String key;
            public String method;
            public String url;
            public int delay;
            public PathVariable path_variable;
            public List<Param> request_params;
            public List<Param> request_body;
            public List<Param> response_params;
        }

        public static class PathVariable {
            public String type_param;
            public String name;
            public String value;
        }

        public static class Param {
            public String type_param;
            public String name;
            public String value;
            public List<Param> params;
        }
    }

    public static Configuration load(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Configuration.class);
    }
}
