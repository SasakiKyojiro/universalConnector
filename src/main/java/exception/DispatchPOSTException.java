package exception;

import config.types.SystemType;

public class DispatchPOSTException extends Exception{
    public DispatchPOSTException(String message, SystemType systemType) {
        super(systemType + ": "+ message);
    }
}
