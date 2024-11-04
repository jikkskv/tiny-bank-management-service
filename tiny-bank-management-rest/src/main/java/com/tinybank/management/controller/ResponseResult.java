package com.tinybank.management.controller;

import com.tinybank.management.exception.BizErrorCodeEnum;
import com.tinybank.management.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult<T> implements Serializable {
    private ErrorCode errorCode = BizErrorCodeEnum.NO_ERROR;
    private String errorMessage = "";
    private T data;

    public ResponseResult(ErrorCode errorCode, T data) {
        this.errorCode = errorCode;
        this.data = data;
    }

    public ResponseResult(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ResponseResult success() {
        return success(Collections.emptyMap());
    }

    public static <T> ResponseResult<T> success(final T data) {
        ResponseResult<T> responseResult = new ResponseResult(BizErrorCodeEnum.NO_ERROR, data);
        return responseResult;
    }

    public static ResponseResult failure(final ErrorCode errorCode) {
        return failure(errorCode, "");
    }

    public static <T> ResponseResult<T> failure(final ErrorCode errorCode, final String errorMessage) {
        ResponseResult<T> responseResult = new ResponseResult(errorCode, errorMessage);
        return responseResult;
    }
}

