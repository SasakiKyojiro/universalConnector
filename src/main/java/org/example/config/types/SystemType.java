package org.example.config.types;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SystemType {
    SYSTEM_TYPE_A,
    SYSTEM_TYPE_B
}
