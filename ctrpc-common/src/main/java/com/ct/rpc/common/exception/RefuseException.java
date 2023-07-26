package com.ct.rpc.common.exception;

/**
 * @author CT
 * @version 1.0.0
 * @description RefuseException
 */
public class RefuseException extends RuntimeException{

    public RefuseException(final Throwable e) {
        super(e);
    }

    public RefuseException(final String message) {
        super(message);
    }

    public RefuseException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
