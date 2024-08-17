package exception;

import config.types.SystemType;

public class DispatchGETException extends Exception{
    public DispatchGETException(String message, SystemType systemType) {
        super(systemType + ": "+ message);
    }
}
