package com.alimate.service;

import com.alimate.dao.PermissionDao;
import com.alimate.dto.UserDetailsAdapter;
import com.alimate.model.Permission;
import com.alimate.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional(readOnly = true)
public class JpaPermissionService implements PermissionService {
    @Autowired private PermissionDao permissionDao;
    @Autowired private SessionRegistry sessionRegistry;

    @Override
    public List<Permission> getAllAssignablePermissions() {
        return permissionDao.findAllAssignablePermissions();
    }

    @Override
    public Permission getPermission(String name) {
        Permission permission = permissionDao.findByName(name);
        if (isNull(permission))
            throw new PermissionNotFoundException();

        return permission;
    }

    @Override
    public boolean exists(String name) {
        return nonNull(permissionDao.findByName(name));
    }

    @Override
    public Page<Permission> getPermissions(int page, int size) {
        if (page < 0)
            throw new IllegalArgumentException("page param can't be negative: " + page);
        if (size <= 0)
            throw new IllegalArgumentException("size param can't be equal or less than zero: " + size);

        return permissionDao.findAll(new PageRequest(page, size));
    }

    @Override
    public Permission getPermission(long id) {
        if (id < 0)
            throw new IllegalArgumentException("id can't be negative: " + id);

        Permission permission = permissionDao.findOne(id);
        if (isNull(permission))
            throw new PermissionNotFoundException();

        return permission;
    }

    @Override
    @Transactional
    public void create(Permission permission) {
        permissionDao.save(permission);
    }

    @Override
    @Transactional
    public void update(Permission update) {
        Permission permission = permissionDao.findOne(update.getId());
        if (isNull(permission))
            throw new PermissionNotFoundException();

        permission.setName(update.getName());
        permission.setAssignable(update.isAssignable());
    }

    @Override
    @Transactional
    public void delete(Permission permission) {
        permissionDao.delete(permission);
        expireSessionIfRequired(permission);
    }

    private void expireSessionIfRequired(Permission permission) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            for (SessionInformation session : sessionRegistry.getAllSessions(principal, true)) {
                UserDetailsAdapter user = (UserDetailsAdapter) session.getPrincipal();
                Set<Permission> perms = user.getUser().getRoles()
                        .stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .collect(toSet());

                if (perms.contains(permission))
                    session.expireNow();
            }
        }
    }
}