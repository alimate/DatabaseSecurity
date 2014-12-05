package com.alimate.model;

import javax.persistence.*;

@Entity
public class Permission extends AbstractEntity {
    private Long id;
    private String name;
    private boolean assignable;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_id_seq")
    @SequenceGenerator(name = "permission_id_seq", sequenceName = "permission_id_seq", allocationSize = 1)
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Permission))
            return false;

        Permission that = (Permission) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Permission(id=%s, name=%s)",
                getId(), getName());
    }
}