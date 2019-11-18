package com.eastlanglearn.east.repository;

import com.eastlanglearn.east.domain.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * from users", nativeQuery = true)
    LinkedHashSet<User> getAllUsers();

    @Modifying
    @Query(value = "DELETE FROM users_roles Where users_roles.user_id=?1", nativeQuery = true)
    void deleteUserInRoles(String id);

    @Modifying
    @Query(value = "DELETE FROM users Where users.id=?1", nativeQuery = true)
    void deleteUserInUsers(String id);

    @Query(value = "Select * from users Order by users.xp Desc ", nativeQuery = true)
    LinkedHashSet<User> getAllUsersByXp();
}
