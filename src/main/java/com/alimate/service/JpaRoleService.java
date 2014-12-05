package com.alimate.service;

import com.alimate.dao.RoleDao;
import com.alimate.dto.UserDetailsAdapter;
import com.alimate.model.Role;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional(readOnly = true)
public class JpaRoleService implements RoleService {
    @Autowired private RoleDao roleDao;
    @Autowired private SessionRegistry sessionRegistry;

    @Override
    public Set<Role> getAllParents(Long id) {
        if (isNull(id))
            throw new NullPointerException("id can't be null");
        if (id < 0)
            throw new IllegalArgumentException("id can't be negative");

        Role role = roleDao.findOne(id);
        if (isNull(role))
            throw new RoleNotFoundException();

        Set<Role> parents = new HashSet<>();
        while (nonNull(role.getParent())) {
            parents.add(role.getParent());
            role = role.getParent();
        }

        return parents;
    }

    @Override
    public Set<Role> getAllChildren(Long id) {
        Assert.notNull(id, "id can't be null");
        Assert.isTrue(id >= 0, "id can't be negative");

        Role role = roleDao.findOne(id);
        if (isNull(role))
            throw new RoleNotFoundException();

        Set<Role> children = new HashSet<>();
        Queue<Role> fifo = new LinkedList<>();
        fifo.add(role);
        while (!fifo.isEmpty()) {
            Role current = fifo.remove();
            List<Role> immediateChildren = roleDao.findByParent(current);

            fifo.addAll(immediateChildren);
            children.addAll(immediateChildren);
        }

        return children;
    }

    @Override
    public List<Role> getAll() {
        return roleDao.findAll();
    }

    @Override
    public Page<Role> getAll(int page, int size) {
        if (page < 0)
            throw new IllegalArgumentException("page param can't less than or equal zero: page=" + page);

        if (size <= 0)
            throw new IllegalArgumentException("size param can't be less than or equal zero: size=" + size);

        return roleDao.findAll(new PageRequest(page, size));
    }

    @Override
    public List<Role> getAllAssignableRoles() {
        return roleDao.findAllAssignableRoles();
    }

    @Override
    @Transactional
    public void create(Role role) {
        roleDao.save(role);

        role.getMutexRoles().stream().map(r -> getRole(r.getId())).forEach(mutex -> {
            mutex.getMutexRoles().add(role);
        });

        checkMutexRolesCompatibility(role, role.getMutexRoles());
    }

    @Override
    @Transactional
    public void update(Role update) {
        if (update.getId() < 0)
            throw new RoleNotFoundException();

        Role role = getRole(update.getId());
        if (isNull(role))
            throw new RoleNotFoundException();

        if (role.equals(update.getParent()))
            throw new CyclicRoleReferenceException();

        if (update.getMutexRoles().contains(role))
            throw new CyclicRoleReferenceException();

        role.setName(update.getName());
        role.setParent(update.getParent());
        role.setPermissions(update.getPermissions());

        role.getMutexRoles().stream().map(r -> getRole(r.getId())).forEach(mutex -> {
            mutex.getMutexRoles().remove(role);
        });
        role.setMutexRoles(update.getMutexRoles());

        role.getMutexRoles().stream().map(r -> getRole(r.getId())).forEach(mutex -> {
            mutex.getMutexRoles().add(role);
        });

        checkMutexRolesCompatibility(role, role.getMutexRoles());
        expireSessionIfRequired(role);
    }

    @Override
    public Role getRole(String name) {
        Role role = roleDao.findByName(name);
        if (isNull(role))
            throw new RoleNotFoundException();

        Hibernate.initialize(role.getMutexRoles());

        return role;
    }

    @Override
    public Role getRole(long id) {
        if (id < 0)
            throw new IllegalArgumentException("id can't be null: " + id);

        Role role = roleDao.findOne(id);
        if (isNull(role))
            throw new RoleNotFoundException();
        Hibernate.initialize(role.getMutexRoles());
        Hibernate.initialize(role.getPermissions());

        return role;
    }

    @Override
    public boolean exists(String name) {
        return nonNull(roleDao.findByName(name));
    }

    @Override
    public void remove(Role role) {
        roleDao.delete(role);
        expireSessionIfRequired(role);
    }

    private void expireSessionIfRequired(Role role) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            for (SessionInformation session : sessionRegistry.getAllSessions(principal, true)) {
                UserDetailsAdapter user = (UserDetailsAdapter) session.getPrincipal();
                if (user.getUser().getRoles().contains(role)) {
                    session.expireNow();
                }
            }
        }
    }

    private void checkMutexRolesCompatibility(Role role, Set<Role> mutexes) {
        Set<Role> incompatibles = getAllParents(role.getId());
        incompatibles.addAll(getAllChildren(role.getId()));

        for (Role mutex : mutexes) {
            if (incompatibles.contains(mutex))
                throw new MutexConflictWithRoleHierarchyException(mutex.getName());
        }
    }
}