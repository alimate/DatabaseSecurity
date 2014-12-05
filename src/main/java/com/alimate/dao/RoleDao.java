package com.alimate.dao;

import com.alimate.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleDao extends JpaRepository<Role, Long> {
    @Query(value = "SELECT * FROM role WHERE assignable IS TRUE", nativeQuery = true)
    List<Role> findAllAssignableRoles();

    Role findByName(String name);

    List<Role> findByParent(Role parent);
}