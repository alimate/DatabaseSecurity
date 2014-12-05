package com.alimate.service;

import com.alimate.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface RoleService {
    Set<Role> getAllParents(Long id);

    Set<Role> getAllChildren(Long id);

    List<Role> getAll();

    Page<Role> getAll(int page, int size);

    List<Role> getAllAssignableRoles();

    void create(Role role);

    void update(Role update);

    Role getRole(String name);

    Role getRole(long id);

    boolean exists(String name);

    void remove(Role role);
}