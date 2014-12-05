package com.alimate.service;

public class MutexConflictWithRoleHierarchyException extends RuntimeException {
    private final String conflictingRole;

    public MutexConflictWithRoleHierarchyException(String conflictingRole) {
        super();
        this.conflictingRole = conflictingRole;
    }

    public String getConflictingRole() {
        return conflictingRole;
    }
}