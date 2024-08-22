package exception;

import config.types.SystemType;

public class DispatchPUTException extends Exception {
    public DispatchPUTException(String message, SystemType systemType) {
        super(systemType + ": " + message);
    }
}
