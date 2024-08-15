package config.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import config.types.ParameterType;

import java.io.IOException;

public class CustomDeserializer extends StdDeserializer<ParameterType> {

    public CustomDeserializer() {
        this(null);
    }

    public CustomDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ParameterType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String dataType = node.asText();

        switch (dataType) {
            case "STRING":
                return ParameterType.STRING;
            case "DATETIME":
                return ParameterType.DATETIME;
            case "AUTH_TOKEN":
                return ParameterType.AUTH_TOKEN;
            case "BOOLEAN":
                return ParameterType.BOOLEAN;
            case "INT":
                return ParameterType.INT;
            case "OBJECT":
                return ParameterType.OBJECT;
            case "LIST|INT":
                return ParameterType.LIST_INT;
            case "LIST|OBJECT":
                return ParameterType.LIST_OBJECT;
            default:
                throw new IllegalArgumentException("Unknown data type: " + dataType);
        }
    }
}
