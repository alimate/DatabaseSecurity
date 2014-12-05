package com.alimate.service;

import com.alimate.model.Permission;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PermissionService {
    List<Permission> getAllAssignablePermissions();

    Permission getPermission(String name);

    boolean exists(String name);

    Page<Permission> getPermissions(int page, int size);

    Permission getPermission(long id);

    void create(Permission permission);

    void update(Permission update);

    void delete(Permission permission);
}