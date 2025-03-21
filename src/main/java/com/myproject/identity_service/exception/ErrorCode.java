package com.myproject.identity_service.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "User existed"),
    UNCATEGORIZED_EXCEPTION(9999, "uncategorized exception"),
    USERNAME_INVALID(1002, "User name invalid,at least 3 characters"),
    PASSWORD_INVALID(1003, "Password invalid,at least 8 characters"),
    INVALID_KEY(1004, "Invalid message key"),
    USER_NOT_EXISTED(1005, "User not found"),
    UNAUTHENTICATED(1006, "Unauthenticated")
    ;
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
