package com.agrawal93.kafka.manager.exception;

/**
 *
 * @author Hardik Agrawal [ hardik93@ymail.com ]
 */
public class InvalidPayloadException extends RuntimeException {

    public InvalidPayloadException(String message) {
        super(message);
    }

    public InvalidPayloadException(Throwable cause) {
        super(cause);
    }

    public InvalidPayloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPayloadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
