package com.alimate.service;

public class UserWithMutexRolesException extends RuntimeException {
    private String role;
    private String other;

    public UserWithMutexRolesException(String role, String other) {
        this.role = role;
        this.other = other;
    }

    public String getRole() {
        return role;
    }

    public String getOther() {
        return other;
    }
}
