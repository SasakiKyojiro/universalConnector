package exception;

import config.types.SystemType;

public class DispatchGetException extends Exception{
    public DispatchGetException(String message, SystemType systemType) {
        super(systemType + ": "+ message);
    }
}
