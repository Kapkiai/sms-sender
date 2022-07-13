package com.kapkiai.smpp.exception;

public class FailedToSubmitMessageException extends RuntimeException {

    public FailedToSubmitMessageException(String message, Throwable cause){
        super(message, cause);
    }

    public FailedToSubmitMessageException(String message){
        super(message);
    }

}
