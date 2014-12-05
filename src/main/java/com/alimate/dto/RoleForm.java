package com.alimate.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class RoleForm {
    private String name;
    private String parent;
    private List<String> mutex = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    @NotNull @NotEmpty(message = "{field.required}")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<String> getMutex() {
        return mutex;
    }

    public void setMutex(List<String> mutex) {
        this.mutex = mutex;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}