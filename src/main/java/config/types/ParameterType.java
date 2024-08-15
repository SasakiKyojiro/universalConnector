package config.types;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ParameterType {
    STRING,
    DATETIME,
    AUTH_TOKEN,
    BOOLEAN,
    INT,
    OBJECT,
    LIST_INT,
    LIST_OBJECT
}
