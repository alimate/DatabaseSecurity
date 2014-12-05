package com.alimate.service;

public class ExistedRoleException extends RuntimeException {
    public ExistedRoleException() {
        super();
    }

    public ExistedRoleException(String message) {
        super(message);
    }

    public ExistedRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistedRoleException(Throwable cause) {
        super(cause);
    }
}
