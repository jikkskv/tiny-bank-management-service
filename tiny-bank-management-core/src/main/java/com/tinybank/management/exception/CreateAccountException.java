package com.tinybank.management.exception;

public class CreateAccountException extends Exception {

    private static final long serialVersionUID = 1L;

    public CreateAccountException() {
        super("Create account failed");
    }

    public CreateAccountException(String message) {
        super(message);
    }
}
