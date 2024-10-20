package org.example.config.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.example.config.types.ParameterType;

import java.io.IOException;

public class CustomDeserializer extends StdDeserializer<ParameterType> {

    public CustomDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ParameterType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String dataType = node.asText();

        return switch (dataType) {
            case "STRING" -> ParameterType.STRING;
            case "DATETIME" -> ParameterType.DATETIME;
            case "AUTH_TOKEN" -> ParameterType.AUTH_TOKEN;
            case "BOOLEAN" -> ParameterType.BOOLEAN;
            case "INT" -> ParameterType.INT;
            case "OBJECT" -> ParameterType.OBJECT;
            case "LIST|INT" -> ParameterType.LIST_INT;
            case "LIST|OBJECT" -> ParameterType.LIST_OBJECT;
            default -> throw new IllegalArgumentException("Unknown data type: " + dataType);
        };
    }
}
