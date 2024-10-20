package org.example.config.parser;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.config.json.Config;
import org.example.config.types.ParameterType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ConfigParser {
    public Config parseConfig(String filePath)  {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ParameterType.class, new CustomDeserializer(ParameterType.class));
        mapper.registerModule(module);
        Config config = null;
        try {
            InputStream is = new FileInputStream(filePath);
            config = mapper.readValue(is, Config.class);
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found: " + filePath);
        } catch (StreamReadException e) {
            System.err.println("Error reading config file: " + filePath);
        } catch (IOException e) {
            System.err.println("File structure error: " + filePath);
        }
        return config;
    }
}
