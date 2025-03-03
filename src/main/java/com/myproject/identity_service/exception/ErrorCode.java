package com.myproject.identity_service.exception;

import org.aspectj.bridge.IMessage;

public enum ErrorCode {
    USER_EXISTED(1001, "User existed"),
    UNCATEGORIZED_EXCEPTION(9999, "uncategorized exception"),
    USERNAME_INVALID(1002, "User name invalid,at least 3 characters"),
    PASSWORD_INVALID(1003, "Password invalid,at least 8 characters"),
    INVALID_KEY(1004, "Invalid message key"),
    ;
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
