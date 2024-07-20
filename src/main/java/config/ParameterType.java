package config;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ParameterType {
    STRING,
    DATETIME,
    AUTH_TOKEN,
    BOOLEAN,
    INT,
    LIST,
    OBJECT,
    LIST_INT,
    LIST_OBJECT
}
