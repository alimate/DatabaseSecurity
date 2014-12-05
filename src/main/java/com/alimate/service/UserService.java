package com.alimate.service;

import com.alimate.model.Role;
import com.alimate.model.User;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface UserService {
    Set<Role> getDeclaredRoles(Long id);

    Set<Role> getInheritedRoles(Long id);

    Set<Role> getAllRoles(Long id);

    Page<User> getAll(int page, int size);

    User getUser(long id);

    boolean exists(String username);

    boolean isEmailRegistered(String email);

    void create(User user, String password);

    User getUser(String username);

    void update(User update, String password);

    void delete(long id);
}