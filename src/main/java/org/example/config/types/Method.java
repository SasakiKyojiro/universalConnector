package org.example.config.types;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Method {
    POST,
    GET,
    PUT,
    NONE
}
