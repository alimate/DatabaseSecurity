package com.alimate.service;

import com.alimate.dao.UserDao;
import com.alimate.model.Permission;
import com.alimate.model.Role;
import com.alimate.model.User;
import com.alimate.dto.UserDetailsAdapter;
import com.alimate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Transactional
public class JpaUserDetailsService implements UserDetailsService {
    @Autowired private UserDao userDao;
    @Autowired private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);

        if (isNull(user))
            throw new UsernameNotFoundException("No such username: " + username);

        return new UserDetailsAdapter(user, getAuthorities(user.getId()));
    }

    private Set<GrantedAuthority> getAuthorities(Long userId) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        Set<Role> roles = userService.getAllRoles(userId);
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            for (Permission perm : role.getPermissions())
                authorities.add(new SimpleGrantedAuthority(perm.getName()));
        }

        return authorities;
    }
}