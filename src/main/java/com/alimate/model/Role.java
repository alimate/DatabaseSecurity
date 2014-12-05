package com.alimate.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role extends AbstractEntity {
    private Long id;
    private String name;
    private boolean assignable;
    private Role parent;
    private Set<Role> mutexRoles = new HashSet<>();
    private Set<Permission> permissions = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", schema = "public", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAssignable() {
        return assignable;
    }

    public void setAssignable(boolean assignable) {
        this.assignable = assignable;
    }

    @ManyToOne
    @JoinColumn
    public Role getParent() {
        return parent;
    }

    public void setParent(Role parent) {
        this.parent = parent;
    }

    @ManyToMany
    @JoinTable(name = "mutex_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "mutex_role_id"))
    public Set<Role> getMutexRoles() {
        return mutexRoles;
    }

    public void setMutexRoles(Set<Role> mutexRoles) {
        this.mutexRoles = mutexRoles;
    }

    @ManyToMany
    @JoinTable(name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Role))
            return false;

        Role role = (Role) o;

        if (id != null ? !id.equals(role.id) : role.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Role(id=%s, name=%s)",
                getId(), getName());
    }
}