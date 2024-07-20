package config;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AuthorizationType {
    AUTH_TOKEN,
    PERMANENT_TOKEN,
    NONE
}
