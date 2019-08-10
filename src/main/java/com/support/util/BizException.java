package com.support.util;

public class BizException extends RuntimeException {

    private String errorMessage;

    public BizException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
