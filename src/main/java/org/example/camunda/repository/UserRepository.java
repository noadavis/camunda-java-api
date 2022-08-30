package org.example.camunda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.example.camunda.app.user.Role;
import org.example.camunda.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.username = :username")
    List<User> checkUsername(@Param("username") String username);

    @Query(value = "SELECT u FROM User u WHERE u.id IN (:list)")
    List<User> getUsersByList(@Param("list") List<Integer> list);
}
