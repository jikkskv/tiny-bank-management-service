package com.tinybank.management.exception;

import lombok.Getter;

@Getter
public enum BizErrorCodeEnum implements ErrorCode {
    NO_ERROR(0, "Success"),
    BAD_DATA(300, "Bad Data or Bad input"),
    SYSTEM_ERROR(500, "System Error"),
    CREATE_ACCOUNT_FAILED(500_001),
    CANCEL_ACCOUNT_FAILED(500_002),
    DEPOSIT_OPERATION_FAILED(500_003),
    WITHDRAW_OPERATION_FAILED(500_004),
    TRANSFER_OPERATION_FAILED(500_005);

    private int code;
    private String message;

    BizErrorCodeEnum(final int code) {
        this.code = code;
    }

    BizErrorCodeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[" + this.getCode() + "]" + this.getMessage();
    }
}
