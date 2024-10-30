package org.example.exception;

import org.example.config.types.SystemType;

public class DispatchGetException extends Exception {
    public DispatchGetException(String message, SystemType systemType) {
        super(systemType + ": " + message);
    }
}
