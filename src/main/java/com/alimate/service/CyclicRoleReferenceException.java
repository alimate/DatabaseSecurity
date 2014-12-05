package com.alimate.service;

public class CyclicRoleReferenceException extends RuntimeException {
    public CyclicRoleReferenceException() {
        super();
    }

    public CyclicRoleReferenceException(String message) {
        super(message);
    }
}