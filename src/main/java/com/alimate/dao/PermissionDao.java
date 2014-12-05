package com.alimate.dao;

import com.alimate.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionDao extends JpaRepository<Permission, Long> {
    @Query(value = "SELECT * FROM permission WHERE assignable IS TRUE", nativeQuery = true)
    List<Permission> findAllAssignablePermissions();

    Permission findByName(String name);
}