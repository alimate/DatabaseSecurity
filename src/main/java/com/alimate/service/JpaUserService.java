package com.alimate.service;

import com.alimate.dao.UserDao;
import com.alimate.dto.UserDetailsAdapter;
import com.alimate.model.Role;
import com.alimate.model.User;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional(readOnly = true)
public class JpaUserService implements UserService {
    @Autowired private UserDao userDao;
    @Autowired private RoleService roleService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private SessionRegistry sessionRegistry;

    @Override
    public Set<Role> getDeclaredRoles(Long id) {
        if (isNull(id))
            throw new NullPointerException("user id can't be null");
        if (id < 0)
            throw new IllegalArgumentException("user id can't be negative: " + id);

        User user = userDao.findOne(id);
        if (isNull(user))
            throw new UserNotFoundException();

        return user.getRoles();
    }

    @Override
    public Set<Role> getInheritedRoles(Long id) {
        Set<Role> declaredRoles = getDeclaredRoles(id);

        Set<Role> inheritedRoles = new HashSet<>();
        for (Role role : declaredRoles)
            inheritedRoles.addAll(roleService.getAllParents(role.getId()));

        return inheritedRoles;
    }

    @Override
    public Set<Role> getAllRoles(Long id) {
        Set<Role> roles = new HashSet<>();
        roles.addAll(getDeclaredRoles(id));
        roles.addAll(getInheritedRoles(id));

        return roles;
    }

    @Override
    public Page<User> getAll(int page, int size) {
        if (page < 0)
            throw new IllegalArgumentException("page parameter can't b negative: " + page);
        if (size < 0)
            throw new IllegalArgumentException("size parameter can't be less or equal than zero: " + size);

        return userDao.findAll(new PageRequest(page, size));
    }

    @Override
    public User getUser(long id) {
        if (id < 0)
            throw new IllegalArgumentException("id parameter can't be negative: " + id);

        User user = userDao.findOne(id);
        if (isNull(user))
            throw new UserNotFoundException();

        Hibernate.initialize(user.getRoles());

        return user;
    }

    @Override
    public boolean exists(String username) {
        return nonNull(userDao.getByUsername(username));
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return nonNull(userDao.getByEmail(email));
    }

    @Override
    @Transactional
    public void create(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        User newUser = userDao.save(user);

        rollbackIfRolesNotCompatible(newUser);
    }

    @Override
    public User getUser(String username) {
        if (isNull(username))
            throw new NullPointerException("username parameter can't be null");

        User user = userDao.getByUsername(username);
        if (isNull(user))
            throw new UserNotFoundException();

        return user;
    }

    @Override
    @Transactional
    public void update(User update, String password) {
        User user = userDao.findOne(update.getId());
        if (isNull(user))
            throw new UserNotFoundException();

        user.setFirstName(update.getFirstName());
        user.setLastName(update.getLastName());
        user.setRoles(update.getRoles());

        if (nonNull(password) && !password.trim().isEmpty())
            user.setPassword(passwordEncoder.encode(password));

        rollbackIfRolesNotCompatible(user);
        expireSessionIfRequired(user);
    }

    @Override
    public void delete(long id) {
        if (id < 0)
            throw new UserNotFoundException();

        User user = userDao.getOne(id);
        if (isNull(user))
            throw new UserNotFoundException();

        userDao.delete(user);
        expireSessionIfRequired(user);
    }

    private void rollbackIfRolesNotCompatible(User user) {
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            for (Role other : roles) {
                if (role.equals(other))
                    continue;

                if (role.getMutexRoles().contains(other))
                    throw new UserWithMutexRolesException(role.getName(), other.getName());

                for (Role parent : roleService.getAllParents(other.getId())) {
                    if (role.getMutexRoles().contains(parent))
                        throw new UserWithMutexRolesException(role.getName(), other.getName());
                }

                for (Role first : roleService.getAllParents(role.getId())) {
                    for (Role sec : roleService.getAllParents(other.getId())) {
                        if (first.getMutexRoles().contains(sec))
                            throw new UserWithMutexRolesException(role.getName(), other.getName());
                    }
                }
            }
        }
    }

    private void expireSessionIfRequired(User user) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            UserDetailsAdapter details = (UserDetailsAdapter) principal;

            if (details.getUser().equals(user))
                for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                    session.expireNow();
                }
        }
    }
}
