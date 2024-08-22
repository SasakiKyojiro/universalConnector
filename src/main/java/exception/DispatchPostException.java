package exception;

import config.types.SystemType;

public class DispatchPostException extends Exception{
    public DispatchPostException(String message, SystemType systemType) {
        super(systemType + ": "+ message);
    }
}
