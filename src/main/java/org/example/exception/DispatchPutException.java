package org.example.exception;

import org.example.config.types.SystemType;

public class DispatchPutException extends Exception {
    public DispatchPutException(String message, SystemType systemType) {
        super(systemType + ": " + message);
    }
}
