package exception;

import config.types.SystemType;

public class ReceivingException extends Exception {
    public ReceivingException(String message, SystemType systemType) {
        super(systemType + ": "+ message);
    }
}
