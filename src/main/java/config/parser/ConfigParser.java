package config.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import config.json.Config;
import config.types.ParameterType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigParser {
    public Config parseConfig(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ParameterType.class, new CustomDeserializer(ParameterType.class));
        mapper.registerModule(module);
        InputStream is = new FileInputStream(filePath);
        return mapper.readValue(is, Config.class);
    }
}
