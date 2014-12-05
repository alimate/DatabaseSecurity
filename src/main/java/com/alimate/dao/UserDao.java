package com.alimate.dao;

import com.alimate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query(value = "SELECT password FROM \"user\" WHERE id = ?1", nativeQuery = true)
    String getPassword(Long id);

    @Query(value = "UPDATE \"user\" SET password = ?2 WHERE id = ?1", nativeQuery = true)
    void updatePassword(Long id, String password);

    User getByUsername(String username);

    User getByEmail(String email);
}