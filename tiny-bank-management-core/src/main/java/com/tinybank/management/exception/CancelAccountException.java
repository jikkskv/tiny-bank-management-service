package com.tinybank.management.exception;

public class CancelAccountException extends Exception {

    private static final long serialVersionUID = 1L;

    public CancelAccountException() {
        super("Cancel account failed");
    }
}
